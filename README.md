# Плагин наката DWH
[![pipeline status](https://gitlab.akb-it.ru/gismubi/DataOps/gradle-dwh-plugin/badges/master/pipeline.svg)](https://gitlab.akb-it.ru/gismubi/DataOps/gradle-dwh-plugin/-/commits/master)
[![Quality Gate Status](http://192.168.233.234:9000/api/project_badges/measure?project=gradle-dwh-plugin&metric=alert_status)](http://192.168.233.234:9000/dashboard?id=gradle-dwh-plugin) 
[![Reliability Rating](http://192.168.233.234:9000/api/project_badges/measure?project=gradle-dwh-plugin&metric=reliability_rating)](http://192.168.233.234:9000/dashboard?id=gradle-dwh-plugin)
[![Security Rating](http://192.168.233.234:9000/api/project_badges/measure?project=gradle-dwh-plugin&metric=security_rating)](http://192.168.233.234:9000/dashboard?id=gradle-dwh-plugin)
[![Technical Debt](http://192.168.233.234:9000/api/project_badges/measure?project=gradle-dwh-plugin&metric=sqale_index)](http://192.168.233.234:9000/dashboard?id=gradle-dwh-plugin)
[![Vulnerabilities](http://192.168.233.234:9000/api/project_badges/measure?project=gradle-dwh-plugin&metric=vulnerabilities)](http://192.168.233.234:9000/dashboard?id=gradle-dwh-plugin)


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
Для создания релиза требуется обновить `baseVersion` в `buildGradle` и заполнить [CHANGELOG.md](CHANGELOG.md), после чего пометить комит тэгом, 
соответствущим `baseVersion`. Помечать тэгом для релиза допускается только комиты в мастере или релизных ветках.