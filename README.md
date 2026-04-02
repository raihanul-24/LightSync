# LightSync

LightSync is a Java Swing desktop app that simulates a smart traffic control system. It provides a public dashboard for real-time signal status and an admin dashboard for managing signal cycles and responding to emergency/criminal events.

## Features
- Login screen with Admin and Public access
- Admin dashboard with signal timer, visual indicator, and action buttons
- Public dashboard that polls the admin dashboard for live status
- Gradient-based UI styling

## Requirements
- Java 17+ (Java 24 works)
- Optional: Apache Ant if you want to use the Ant targets

## Run
### Option 1: Run from compiled classes
```
java -cp build/classes lightsync.LightSync
```

### Option 2: Use Ant (if installed)
```
ant run
```

## Admin Credentials
- Username: admin
- Password: 1234

## Project Layout
- src/lightsync/LightSync.java: main application and UI
- src/lightsync/criminal_plates.txt: data used by the admin dashboard
- build/classes: compiled classes output (if already built)

## Notes
If the UI does not appear, build the project first or ensure you are running from a Java installation on your PATH.
