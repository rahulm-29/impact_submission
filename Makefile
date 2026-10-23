# Build and test with plain javac + the JUnit 5 console launcher (no Maven/Gradle).
# Requires: JDK 8+, make, curl (only the first time, to fetch the JUnit jar).

JUNIT_VERSION := 1.10.2
JUNIT_JAR     := lib/junit-platform-console-standalone-$(JUNIT_VERSION).jar
JUNIT_URL     := https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/$(JUNIT_VERSION)/junit-platform-console-standalone-$(JUNIT_VERSION).jar

MAIN_OUT := out/main
TEST_OUT := out/test

MAIN_SRC := $(wildcard src/*.java)
TEST_SRC := $(wildcard tests/*.java)

.PHONY: test compile compile-tests clean

test: compile-tests
	java -jar $(JUNIT_JAR) execute --class-path $(MAIN_OUT):$(TEST_OUT) --scan-class-path $(TEST_OUT)

compile: $(MAIN_SRC)
	mkdir -p $(MAIN_OUT)
	javac -d $(MAIN_OUT) $(MAIN_SRC)

compile-tests: compile $(JUNIT_JAR) $(TEST_SRC)
	mkdir -p $(TEST_OUT)
	javac -d $(TEST_OUT) -cp $(MAIN_OUT):$(JUNIT_JAR) $(TEST_SRC)

$(JUNIT_JAR):
	mkdir -p lib
	curl -fsSL -o $@ $(JUNIT_URL)

clean:
	rm -rf out
