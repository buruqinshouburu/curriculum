$ErrorActionPreference = 'Stop'
$repo = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../../..'))
$output = Join-Path $repo 'csys-service/target/course-code-regression'
New-Item -ItemType Directory -Force -Path $output | Out-Null
$junit = @(Get-ChildItem 'D:/repository/org/junit' -Recurse -Filter '*.jar' |
    Where-Object { $_.FullName -match '\\(5\.12\.2|1\.12\.2)\\' -and $_.Name -notlike '*-sources.jar' } |
    Select-Object -ExpandProperty FullName)
$jars = @(Get-ChildItem 'C:/Users/litj/.m2/repository' -Recurse -Filter '*.jar' |
    Where-Object { $_.FullName -notmatch '\\org\\(apache\\maven|codehaus|sonatype|eclipse|junit)' } |
    Select-Object -ExpandProperty FullName)
$classpath = (@($output, (Join-Path $repo 'csys-service/target/backend-rule-regression'),
    'C:/Users/litj/.m2/repository/commons-io/commons-io/2.15.1/commons-io-2.15.1.jar',
    'C:/Users/litj/.m2/repository/org/apache/commons/commons-compress/1.25.0/commons-compress-1.25.0.jar',
    (Join-Path (Split-Path $repo) 'curriculum-build-libs/*'),
    (Join-Path (Split-Path $repo) 'curriculum-latest/csys-service/target/classes'),
    (Join-Path (Split-Path $repo) 'curriculum-latest/csys-service-api/target/classes')) + $junit + $jars) -join ';'
$sourcepath = @((Join-Path $repo 'csys-service/src/main/java'), (Join-Path $repo 'csys-service-api/src/main/java')) -join ';'
$compile = @('--release', '11', '-encoding', 'UTF-8', '-cp', $classpath, '-sourcepath', $sourcepath, '-d', $output,
    (Join-Path $repo 'csys-service/src/main/java/com/doinner/csys/service/impl/CurriculumServiceImpl.java'),
    (Join-Path $PSScriptRoot 'java/com/doinner/csys/CourseCodeUpdateTest.java'),
    (Join-Path $PSScriptRoot 'java/com/doinner/csys/PracticeImportCodeTest.java'))
[IO.File]::WriteAllLines("$output/compile.args", @($compile | ForEach-Object { '"' + $_.Replace('\', '/') + '"' }), [Text.UTF8Encoding]::new($false))
& javac "@$output/compile.args"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
$run = @("-Dlogback.configurationFile=$repo/csys-service/target/backend-rule-regression/logback-tests.xml",
    '-cp', $classpath, 'BackendRuleRegressionRunner', 'com.doinner.csys.CourseCodeUpdateTest', 'com.doinner.csys.PracticeImportCodeTest')
[IO.File]::WriteAllLines("$output/run.args", @($run | ForEach-Object { '"' + $_.Replace('\', '/') + '"' }), [Text.UTF8Encoding]::new($false))
& java "@$output/run.args"
exit $LASTEXITCODE
