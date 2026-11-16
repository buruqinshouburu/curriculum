$ErrorActionPreference = 'Stop'
$repo = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../../..'))
$output = Join-Path $repo 'csys-service/target/invocation-scope'
New-Item -ItemType Directory -Force -Path $output | Out-Null
$junit = @(Get-ChildItem 'D:/repository/org/junit' -Recurse -Filter '*.jar' |
    Where-Object { $_.FullName -match '\\(5\.12\.2|1\.12\.2)\\' -and $_.Name -notlike '*-sources.jar' } |
    Select-Object -ExpandProperty FullName)
$jars = @(Get-ChildItem 'C:/Users/litj/.m2/repository' -Recurse -Filter '*.jar' |
    Where-Object { $_.FullName -notmatch '\\org\\(apache\\maven|codehaus|sonatype|eclipse|junit)' } |
    Select-Object -ExpandProperty FullName)
$classpath = (@($output,
    (Join-Path $repo 'csys-service/target/backend-rule-regression'),
    (Join-Path (Split-Path $repo) 'curriculum-build-libs/*'),
    (Join-Path (Split-Path $repo) 'curriculum-latest/csys-service/target/classes'),
    (Join-Path (Split-Path $repo) 'curriculum-latest/csys-service-api/target/classes')) + $junit + $jars) -join ';'
$compileArgs = @('--release', '11', '-encoding', 'UTF-8', '-cp', $classpath, '-d', $output,
    (Join-Path $repo 'csys-service/src/main/java/com/doinner/csys/service/impl/CourseServiceImpl.java'),
    (Join-Path $repo 'csys-service/src/main/java/com/doinner/csys/entity/csys/model/DictContent.java'),
    (Join-Path $PSScriptRoot 'java/com/doinner/csys/CourseInvocationScopeTest.java'))
$compileArgs | ForEach-Object { '"' + $_.Replace('\', '/') + '"' } | Set-Content "$output/compile.args" -Encoding utf8
& javac "@$output/compile.args"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
$runArgs = @("-Dlogback.configurationFile=$repo/csys-service/target/backend-rule-regression/logback-tests.xml",
    '-cp', $classpath, 'BackendRuleRegressionRunner', 'com.doinner.csys.CourseInvocationScopeTest')
$runArgs | ForEach-Object { '"' + $_.Replace('\', '/') + '"' } | Set-Content "$output/run.args" -Encoding utf8
& java "@$output/run.args"
exit $LASTEXITCODE
