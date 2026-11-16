$ErrorActionPreference = 'Stop'
$repo = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../../..'))
$output = Join-Path $repo 'csys-service/target/whole-major-export'
$cache = Join-Path $env:USERPROFILE '.m2/repository'
$jars = @(Get-ChildItem $cache -Recurse -Filter '*.jar' -File |
    Where-Object { $_.FullName -notmatch '\\org\\(apache\\maven|codehaus|sonatype|eclipse|junit)|commons-lang3[\\/](3\.7|3\.14\.0)|commons-io[\\/]2\.6' } |
    Select-Object -ExpandProperty FullName)
$classpath = (@($output,
    (Join-Path $repo 'csys-service/target/textbook-regression/junit-platform-console-standalone-1.8.2.jar'),
    (Join-Path $cache 'commons-io/commons-io/2.15.1/commons-io-2.15.1.jar'),
    (Join-Path $cache 'org/apache/commons/commons-compress/1.25.0/commons-compress-1.25.0.jar'),
    (Join-Path (Split-Path $repo) 'curriculum-build-libs/*'),
    (Join-Path (Split-Path $repo) 'curriculum-latest/csys-service/target/classes'),
    (Join-Path (Split-Path $repo) 'curriculum-latest/csys-service-api/target/classes')) + $jars) -join ';'
$sourcepath = @((Join-Path $repo 'csys-service/src/main/java'),
    (Join-Path $repo 'csys-service-api/src/main/java')) -join ';'
New-Item -ItemType Directory -Force -Path $output | Out-Null
$quote = { param($value) '"' + ($value -replace '\\', '/') + '"' }
$sources = @(
    'csys-service-api/src/main/java/com/doinner/csys/domain/Course.java',
    'csys-service/src/main/java/com/doinner/csys/entity/csys/model/TrainingPlanModel.java',
    'csys-service/src/main/java/com/doinner/csys/entity/csys/TrainingPlanGenerator.java',
    'csys-service/src/main/java/com/doinner/csys/entity/csys/NcoTrainingPlanGenerator.java',
    'csys-service/src/main/java/com/doinner/csys/service/impl/TrainingServiceImpl.java',
    'csys-service/src/test/java/com/doinner/csys/TrainingWholeMajorExportTest.java')
$compile = @('--release', '11', '-encoding', 'UTF-8', '-cp', (& $quote $classpath),
    '-sourcepath', (& $quote $sourcepath), '-d', (& $quote $output)) +
    @($sources | ForEach-Object { & $quote (Join-Path $repo $_) })
[IO.File]::WriteAllLines((Join-Path $output 'compile.args'), $compile, [Text.UTF8Encoding]::new($false))
& javac "@$output/compile.args"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
$run = @('-cp', (& $quote $classpath), 'org.junit.platform.console.ConsoleLauncher',
    '--select-class', 'com.doinner.csys.TrainingWholeMajorExportTest', '--disable-ansi-colors', '--details=summary')
[IO.File]::WriteAllLines((Join-Path $output 'run.args'), $run, [Text.UTF8Encoding]::new($false))
& java "@$output/run.args" *> (Join-Path $output 'results.txt')
$result = $LASTEXITCODE
Get-Content (Join-Path $output 'results.txt') -Tail 80
exit $result
