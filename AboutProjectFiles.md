First, to develop, you need Eclipse (for example JEE) with latest JDK and then Android SDK installed. Current version (1.5) of TripComputer is compatible with SDK version 1.5, so you need at least SDK 1.5 to build project.

  * [Sun JDK](http://java.sun.com/javase/downloads/index.jsp)
  * [Eclipse](http://www.eclipse.org/downloads/)
  * [Android SDK](http://developer.android.com/sdk/index.html)

### Resources ###

One of non-standard project folder is "gfx".

There are all SVG graphics files, made with Inkscape and exported to res/drawable as PNG.

Folder res/values-pl has Polish translation data. Its content should be localized after synchronizing with res/values folder.

### Most important packages details ###

  * **pl.tripcomputer**
    1. Command: main command dispatcher
    1. CommandData: command parameter object
    1. Main: commands implementation
    1. MainState: running state of UI objects
    1. MainThread: drawing and processing loop
    1. MainView: extended SurfaceView for drawing
    1. Preferences: settings manager
    1. ServiceDataReceiver: gets location from service

  * **pl.tripcomputer.custom**
    1. EditGeoPos: implementation of location edits layout

  * **pl.tripcomputer.data.common**
    1. Database: main database object

  * **pl.tripcomputer.gps**
    1. GpsReader: abstract base for service and activity location readers

  * **pl.tripcomputer.http**
    1. ClientInterface: object for communicating with GAE webservice

  * **pl.tripcomputer.layers**
    1. Layer: base abstract object for drawing layer objects (bearing, compass, cross, grid, info, location, stats, tracks, waypoints)

  * **pl.tripcomputer.loader**
    1. DataLoader: main loader objects, holding all database data in specialized collections for fast processing

  * **pl.tripcomputer.map**
    1. Mercator: position projection class, that converts sphere wgs84 to flat position
    1. Screen: flat Mercator position to screen coords
    1. Viewport: flat projection view manager

  * **pl.tripcomputer.service**
    1. TripComputerService: main Service module for recording track data in background

  * **pl.tripcomputer.ui**
    1. UIElement: base User Interface object for drawing
    1. UIButton: base UI drawable button object
    1. UIScreen: drawable UI container

  * **pl.tripcomputer.webservice**
    1. WebServiceOperation: abstract class for creating web operations (download/send waypoints, etc..) on GAE web service

That's not all modules and packages, there is a lot more. Just browse sources and all (I hope) become clear !