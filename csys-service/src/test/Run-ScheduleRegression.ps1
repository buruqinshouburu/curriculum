$ErrorActionPreference = 'Stop'
$taskRepo = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$taskOutput = Join-Path $taskRepo 'csys-service/target/schedule-regression'
$taskCache = Join-Path $env:USERPROFILE '.m2/repository'
$taskSibling = Split-Path $taskRepo
$taskJunit = Join-Path $taskRepo 'csys-service/target/textbook-regression/junit-platform-console-standalone-1.8.2.jar'
$taskH2 = Join-Path $taskRepo 'csys-service/target/textbook-regression/h2-2.1.214.jar'
foreach ($taskDependency in @($taskJunit, $taskH2)) {
    if (!(Test-Path -LiteralPath $taskDependency)) { throw "Missing cached test dependency: $taskDependency" }
}
$taskJars = @(Get-ChildItem -LiteralPath $taskCache -Recurse -Filter '*.jar' -File |
    Where-Object {
        $_.FullName -notmatch '\\org\\(apache\\maven|codehaus|sonatype|eclipse|apache\\xmlgraphics|bouncycastle|mongodb|lucene|tukaani|junit)|\\com\\hankcs|\\io\\netty|\\io\\prometheus|commons-lang3[\\/](3\.7|3\.14\.0)|commons-io[\\/]2\.6'
    } | Select-Object -ExpandProperty FullName)
$taskClasspath = (@(
    $taskOutput
    (Join-Path $taskRepo 'csys-service/src/main/resources')
    $taskJunit
    $taskH2
    (Join-Path $taskCache 'commons-io/commons-io/2.15.1/commons-io-2.15.1.jar')
    (Join-Path $taskCache 'org/apache/commons/commons-compress/1.25.0/commons-compress-1.25.0.jar')
    (Join-Path $taskCache 'org/mongodb/bson/4.6.1/bson-4.6.1.jar')
    (Join-Path $taskSibling 'curriculum-build-libs/*')
    (Join-Path $taskSibling 'curriculum-latest/csys-service/target/classes')
    (Join-Path $taskSibling 'curriculum-latest/csys-service-api/target/classes')
) + $taskJars) -join ';'
$taskSourcepath = @(
    (Join-Path $taskRepo 'csys-service/src/main/java')
    (Join-Path $taskRepo 'csys-service-api/src/main/java')
) -join ';'
$taskSources = @(
    (Join-Path $taskRepo 'csys-service/src/test/java/com/doinner/csys/CourseScheduleDependencyTest.java')
    (Join-Path $taskRepo 'csys-service-api/src/main/java/com/doinner/csys/domain/Course.java')
    (Join-Path $taskRepo 'csys-service-api/src/main/java/com/doinner/csys/domain/vo/TrainingSchemeCourseScheduleVo.java')
    (Join-Path $taskRepo 'csys-service/src/main/java/com/doinner/csys/service/impl/TrainingServiceImpl.java')
    (Join-Path $taskRepo 'csys-service/src/main/java/com/doinner/csys/service/impl/CurriculumServiceImpl.java')
    (Join-Path $taskRepo 'csys-service/src/main/java/com/doinner/csys/service/impl/CourseServiceImpl.java')
    (Join-Path $taskRepo 'csys-service/src/main/java/com/doinner/csys/handler/CourseMajorIdsTypeHandler.java')
    (Join-Path $taskRepo 'csys-service/src/test/java/com/doinner/csys/CourseMajorSelectionTest.java')
    (Join-Path $taskRepo 'csys-service/src/test/java/com/doinner/csys/CourseInvocationScopeTest.java')
    (Join-Path $taskRepo 'csys-service/src/test/java/com/doinner/csys/SourceCourseScheduleSyncTest.java')
    (Join-Path $taskRepo 'csys-service/src/test/java/com/doinner/csys/CourseCreditRulesTest.java')
    (Join-Path $taskRepo 'csys-service/src/test/java/com/doinner/csys/TrainingScheduleContractTest.java')
)
New-Item -ItemType Directory -Force -Path $taskOutput | Out-Null
$taskQuote = { param($value) '"' + ($value -replace '\\', '/') + '"' }
$taskCompile = @('--release', '11', '-encoding', 'UTF-8', '-cp', (& $taskQuote $taskClasspath),
    '-sourcepath', (& $taskQuote $taskSourcepath), '-d', (& $taskQuote $taskOutput)) +
    @($taskSources | ForEach-Object { & $taskQuote $_ })
$taskCompileFile = Join-Path $taskOutput 'compile.args'
[System.IO.File]::WriteAllLines($taskCompileFile, $taskCompile, (New-Object System.Text.UTF8Encoding($false)))
& javac "@$taskCompileFile"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
$taskRun = @('-cp', (& $taskQuote $taskClasspath), 'org.junit.platform.console.ConsoleLauncher',
    '--select-class', 'com.doinner.csys.CourseScheduleDependencyTest',
    '--select-class', 'com.doinner.csys.TrainingScheduleContractTest',
    '--select-class', 'com.doinner.csys.SourceCourseScheduleSyncTest',
    '--select-class', 'com.doinner.csys.CourseMajorSelectionTest',
    '--select-class', 'com.doinner.csys.CourseInvocationScopeTest',
    '--select-method', (& $taskQuote 'com.doinner.csys.CourseCreditRulesTest#sourceUpdateNormalizesUsingStoredTypeAndOptionalAttributeWhenOmitted'),
    '--select-method', (& $taskQuote 'com.doinner.csys.CourseCreditRulesTest#libraryNormalizationDoesNotAddChangesToExistingInvocationPropagation'),
    '--disable-ansi-colors', '--details=summary')
$taskLogConfig = Join-Path $taskRepo 'csys-service/target/backend-rule-regression/logback-tests.xml'
if (Test-Path -LiteralPath $taskLogConfig) {
    $taskRun = @(& $taskQuote "-Dlogback.configurationFile=$taskLogConfig") + $taskRun
}
$taskRunFile = Join-Path $taskOutput 'run.args'
[System.IO.File]::WriteAllLines($taskRunFile, $taskRun, (New-Object System.Text.UTF8Encoding($false)))
& java "@$taskRunFile" *> (Join-Path $taskOutput 'results.txt')
$taskExit = $LASTEXITCODE
Get-Content -LiteralPath (Join-Path $taskOutput 'results.txt') -Tail 75
exit $taskExit
