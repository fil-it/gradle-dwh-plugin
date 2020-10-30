package liquibase.dwh.changelog;

import liquibase.dwh.changelog.model.imp.ImportSqlBlock;
import liquibase.dwh.changelog.model.imp.ImportSqlFile;
import org.gradle.api.GradleException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ImportSqlTaskTest {

    @Test
    public void sliceTestFail() {
        ArrayList<ImportSqlBlock> blocks = new ArrayList<>();

        blocks.add(ImportSqlBlock.builder().header("Header: header").line("block1 line1").build());

        Assertions.assertThrows(GradleException.class, () -> ImportSqlTask.sliceBlocks(blocks, "", ""));
    }

    @Test
    public void sliceTestOneBlock() {
        ArrayList<ImportSqlBlock> blocks = new ArrayList<>();

        blocks.add(ImportSqlBlock.builder().header("Created on: begin block").line("block1 line1").build());

        List<ImportSqlFile> exportFiles = ImportSqlTask.sliceBlocks(blocks, "", "");
        Assertions.assertEquals(1, exportFiles.size());
    }

    @Test
    public void sliceTestTwoBlocks() {
        ArrayList<ImportSqlBlock> blocks = new ArrayList<>();

        blocks.add(ImportSqlBlock.builder().header("Table:Table").line("block1 line1").build());
        blocks.add(ImportSqlBlock.builder().header("View:view").line("block2 line1").build());

        List<ImportSqlFile> exportFiles = ImportSqlTask.sliceBlocks(blocks, "", "");
        Assertions.assertEquals(2, exportFiles.size());
    }

    @Test
    public void sliceTestMultipleFiles() {
        ArrayList<ImportSqlBlock> blocks = new ArrayList<>();

        blocks.add(ImportSqlBlock.builder().header("Created on: begin block").line("block1 line1").build());
        blocks.add(ImportSqlBlock.builder().header("Same: block continues").line("block1 line2").build());
        blocks.add(ImportSqlBlock.builder().header("Table:Table").line("block2 line1 single-line block").build());
        blocks.add(ImportSqlBlock.builder().header("View:view").line("block3 line1").build());
        blocks.add(ImportSqlBlock.builder().line("block3 line1 multiline block").build());
        blocks.add(ImportSqlBlock.builder().line("block3 line1 multi-multiline block").build());

        List<ImportSqlFile> exportFiles = ImportSqlTask.sliceBlocks(blocks, "", "");
        Assertions.assertEquals(3, exportFiles.size());
    }
}
