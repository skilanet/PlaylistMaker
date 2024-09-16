# Playlist Maker

Читайте этот файл на других языках:
- [Русский](docs/ru/README.md)

## Overview

This application was created to explore various technologies in the field of Android development. 
 - It uses the iTunes api to get a list of tracks.
 - It has the ability to listen to a 30-second segment of a track.
 - The last 10 tracks are saved to the history. You have ability to clear all history
 - Tracks can be added to favorites. Also you can delete track from history
 - You can also create a playlist and add a track to it. You can delete created playlist

## Technology stack

* Programming language: Kotlin
* Architecture: Clean Architecture, MVVM
* Network Requests: Retrofit
* Asynchronous programming: Kotlin Coroutines, Flow
* UI: XML
* Navigation: Single Activity + Jetpack Navigation
* DI: Koin
* Data Storage: SharedPreference, Room, Internal Storage
* Other: Glide, Peko, ViewPager2, LiveData

## Installation

The application is adapted to most mobile devices with minSdk = 26 (Android 8.0)

[<img src="https://github.com/user-attachments/assets/da5473b2-f392-4d9e-82c8-733809cc06ba"
    alt="Get it on GitHub"
    height="80">](https://github.com/skilanet/PlaylistMaker/releases/tag/v1.0.0)
