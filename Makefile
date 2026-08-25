.PHONY: install dev preview build

install:
	cd android-app && ./gradlew dependencies

dev:
	cd android-app && ./gradlew installDebug

preview:
	cd android-app && ./gradlew installDebug

build:
	cd android-app && ./gradlew assembleDebug
