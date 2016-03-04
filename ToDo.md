**1. OpenStreetMap and other (topo) maps off-line handling**

  * required tool for creating maps cache and data file
  * map tiles should be saved to sd card
  * loading and displaying tiles in a view
  * selectable map projection ?


**2. Adding waypoints connected to track currently recording**

  * selecting wpt connection in wpt edit dialog (alone/connected to current track)


**3. Adding new waypoint with location based on projection**

  * entering direction and distance
  * additional checkbox "Project target" enables controls Distance, Bearing and Calculate


**4. New menu option _Send SMS With Location_**

  * Body example: TripComputer. Sender location: 53.0000000000, 15.0000000000, 124. (UID\_HASH).
  * UID\_HASH is created from content.
  * After app broadcast intent receiver gets sms int this form, UID\_HASH is verified and System Notification is fired, that shows on status bar.
  * After SN clicked by user, dialogs comes up: "Do you want to add new waypoint received from USER\_X ?".
  * After confirmation, new waypoint is created in database with phone number/contact name, sms time, "person" type.


**5. Sending tracks to web service**


**6. Adding photos to waypoints**


**7. UI improvements**


**8. Launcher icon in 2.0 SDK style**


**9. Kalman Filter for improving GPS readings**

  * implementation in NDK ?


**10. Creating ROUTES (simple track made of waypoints) on web map service and downloading ROUTE to phone**


**11. GPX import**

**12. Notification/alarm if selected waypoint in range (talks distance)**


**13. Navigation mode _Follow Track_ - phone talks if you are going off track**


**14. Navigation mode _Return Home_**


**15. Proper handling all devices, SDK higher than 1.6**


**16. Proper handling of HI-RES devices screens**


**17. Trips history**

**18. Improve service lifetime to stay alive or cyclic awake for gps pooling**

  * Unfortunately, system can stop service at any time, so recording is stopped !

### Project is waiting for your own idea ! ###