package liquibase.dwh.changelog;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.StringUtils;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.internal.impldep.org.eclipse.jgit.lib.Constants;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Объект для доступа к параметрам git
 */
public class GitMetaProvider {
    private static File getRootGitDir(File currentRoot) {
        File gitDir = scanForRootGitDir(currentRoot);
        if (!gitDir.exists()) {
            throw new IllegalArgumentException("Cannot find '.git' directory");
        }
        return gitDir;
    }

    private static File scanForRootGitDir(File currentRoot) {
        File gitDir = new File(currentRoot, ".git");
        if (gitDir.exists()) {
            return gitDir;
        }
        // stop at the root directory, return non-existing File object;
        if (currentRoot.getParentFile() == null) {
            return gitDir;
        }
        // look in parent directory;
        return scanForRootGitDir(currentRoot.getParentFile());
    }

    static String extractAuthor(File rootDir, Logger logger) {
        String author = new GitMetaProvider().getAuthor(rootDir);
        if (StringUtils.isEmptyOrNull(author)) {
            throw new GradleException("Can't extract author from git project config");
        } else {
            logger.quiet("Author of changesets is " + author);
            return author;
        }
    }

    static String extractTaskName(File rootDir, Logger logger) {
        String branchName;
        try {
            branchName = new GitMetaProvider().getBranchName(rootDir);
        } catch (RuntimeException e) {
            throw new GradleException("Cannot extract branch name", e);
        }
        String regex = "^(?:feature|bugfix|hotfix|doc)/(GISMUBI-\\d+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(branchName);
        if (matcher.matches()) {
            String task = matcher.group(1);
            logger.quiet("Task number is " + task);
            return task;
        } else {
            throw new GradleException("Can't extract taskName number with regex " + regex);
        }
    }

    /**
     * Извлекает имя ветки по пути корня репозитория
     *
     * @param root путь к корню репозиторию
     * @return имя ветки
     */
    private String getBranchName(File root) {
        Git git = gitRepo(root);
        Ref ref;
        try {
            ref = git.getRepository().findRef(git.getRepository().getBranch());
        } catch (IOException e) {
            throw new GradleException("Can't extract branch name", e);
        }
        if (ref == null) {
            return null;
        }
        return ref.getName().substring(Constants.R_HEADS.length());
    }

    /**
     * Извлекает имя автора по пути корня репозитория
     *
     * @param root путь к корню репозиторию
     * @return имя автора
     */
    private String getAuthor(File root) {
        Git git = gitRepo(root);
        return git.getRepository().getConfig().getString("user", null, "name");
    }

    private Git gitRepo(File root) {
        try {
            File gitDir = getRootGitDir(root);
            Git git;
            try (Repository repo = FileRepositoryBuilder.create(gitDir)) {
                git = new Git(repo);
            }
            return git;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
