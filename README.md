# Плагин наката DWH
[![pipeline status](https://gitlab.akb-it.ru/gismubi/DataOps/gradle-dwh-plugin/badges/master/pipeline.svg)](https://gitlab.akb-it.ru/gismubi/DataOps/gradle-dwh-plugin/-/commits/master)
[![Quality Gate Status](http://192.168.233.234:9000/api/project_badges/measure?project=gradle-dwh-plugin&metric=alert_status)](http://192.168.233.234:9000/dashboard?id=gradle-dwh-plugin) 
[![Reliability Rating](http://192.168.233.234:9000/api/project_badges/measure?project=gradle-dwh-plugin&metric=reliability_rating)](http://192.168.233.234:9000/dashboard?id=gradle-dwh-plugin)
[![Security Rating](http://192.168.233.234:9000/api/project_badges/measure?project=gradle-dwh-plugin&metric=security_rating)](http://192.168.233.234:9000/dashboard?id=gradle-dwh-plugin)
[![Technical Debt](http://192.168.233.234:9000/api/project_badges/measure?project=gradle-dwh-plugin&metric=sqale_index)](http://192.168.233.234:9000/dashboard?id=gradle-dwh-plugin)
[![Vulnerabilities](http://192.168.233.234:9000/api/project_badges/measure?project=gradle-dwh-plugin&metric=vulnerabilities)](http://192.168.233.234:9000/dashboard?id=gradle-dwh-plugin)

## Как пользоваться
### Подключение к проекту
В `settings.gradle` проекта добавляем источник библиотек с плагином
```groovy
pluginManagement {
     repositories { 
          maven { //Для релизных библиотек
               url = REPO_RESOLVE_RELEASE
               credentials {
                    username PHOENIX_USER
                    password PHOENIX_PASSWORD
               }
          }
          maven { //Для снэпшотных библиотек
               url = REPO_RESOLVE_SNAPSHOT
               credentials {
                    username PHOENIX_USER
                    password PHOENIX_PASSWORD
               }
          }
     }
}
```

После этого в `build.gradle` добавляем идентификатор в секцию подключения плагинов
```groovy
plugins {
    //...
    id 'liquibase.dwh.changelog'
    //...
}
```
### Конфигурирование активностей
```groovy
changelog { //Блок конфигурации плагина
    p1 { //Конфигурация предметной области p1, имя может быть произвольным, количество активностей произвольное
        sources { //Блок конфигурации кода, относящегося к данной предметной области, см Gradle CopySpec 
            exclude '**/file1*'

            from('src/main/gp/dir1') {
                exclude '**/file1*'
            }
            from('src/main/gp/p1.yaml')
        }
        activity { //Блок конфигурации активности liquibase, смотри официальный плагин Liquibase
            if (project.hasProperty('contexts')) { //Пример того, что можно писать произвольный код groovy в конфигурационном блоке
                contexts contexts //вызов любого метода кроме changeLogParameters превращается в аргумент ликвибейза, где ключ - имя метода, значение - аргументы метода
            }
            changeLogFile 'src/main/gp/p1.yaml'
            url "$GP_JDBC_BASE_URL/gpadmin" //Можно интерполировать строки из переменных проекта (передаются ключиком -З)
            username(GP_ADMIN_USERNAME) //Для понимания синтаксиса - аргументы можно окружать скобками, а можно скобки опускать
            password GP_ADMIN_PASSWORD // тут скобки оппущены
            logLevel 'warning' //Откуда брать аргументы - из документации ликвибейза!
            includeSchema true // Добавление имени схемы в master.yaml. Необходимо для работы некоторых задач плагина.
            schemas 'schema1,schema2' // Список всех схем. Необходим только при генерации изначального master.yaml с помощью команды generateChangelog.
        }
    }
}
```
### Выбор предметной области при запуске задач Liquibase
Чтобы определять требуемую предметную область требуется задавать ее имя через переменную runList. Для этого требуется добавить блок конфигурации вида:
```groovy
liquibase {
    runList = project.ext.runList
}
```
и при выхове `Gradle` добавлять переменную `-PrunList=AP_NAME` с указанием интересующей предметной области или набора областей через запятую.
Отсутствие явно заданной переменной интерпретируется как все предметные области сразу.

### Какие задачи Liquibase можно использовать
Список задач Gradle доступен в секции `liquibase`
Ознакомится с их назначением можно [по ссылке](https://docs.liquibase.com/commands/community/home.html).
Например, для наката предметной области `p1`, описанной выше, требуется выполнить команду%
```shell
./gradlew update -PrunList=p1
```

## Как разрабатывать
### Monkey testing
Для ручного тестирование можно использовать тестовый проект [test-project](test-project).
Он подключает к себе данный плагин через композитный билд, пожтому откры именно этот проект в среде разработки вы получите удобное окружение.
В остальном ориентируйтесь на официальные документы [Gradle](https://docs.gradle.org/current/userguide/testing_gradle_plugins.html).

## CI
### Настройки
Для корректной работы CI требуется задать следующие переменные в настройках проекта `Settings - CI/CD - Variables.

Для Gradle:
* `PHOENIX_USER` - Имя пользователя для чтения и публикации зависимостей в репозитории
* `PHOENIX_PASSWORD` - Пароль для пользователя `PHOENIX_USER` 
* `REPO_RESOLVE_RELEASE`, `REPO_RESOLVE_SNAPSHOT` - Адрес со всеми зависимостями проекта и используемых при билде плагинами
* `REPO_PUBLISH_RELEASE`, `REPO_PUBLISH_SNAPSHOT` - Адрес для публикации плагинов из проекта

Для CI:
* `SONAR_HOST_URL` - Адрес SonarQube
* `SONAR_TOKEN` - токен для взаимодействия с SonarQube
* `GITLAB_API_TOKEN` - токен для взаимодействия с API Gitlab для данного проекта
* `MOUNT_BASE` - адрес базовой директории, которую можно использовать как кэш. *Не* должен оканчиваться на слэш.

### Релиз
Для создания релиза требуется обновить `baseVersion` в `build.gradle` и заполнить [CHANGELOG.md](CHANGELOG.md), после чего пометить комит тэгом, 
соответствущим `baseVersion`. Помечать тэгом для релиза допускается только комиты в мастере или релизных ветках.