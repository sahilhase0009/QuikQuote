@echo off
set MAVEN_CMD="C:\Users\hases\.gemini\antigravity\scratch\ai-realty-assistant\.maven\apache-maven-3.9.6\bin\mvn.cmd"

if exist %MAVEN_CMD% (
    %MAVEN_CMD% spring-boot:run
) else (
    mvn spring-boot:run
)
