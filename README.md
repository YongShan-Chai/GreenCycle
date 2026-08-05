# GreenCycle

GreenCycle is a Java 8 JavaFX desktop application for managing community
recycling and waste collection bookings.

Residents can register accounts, book recycling pickups, view their bookings,
track reward points, and update their profiles. Administrators can manage
residents, update booking statuses, view dashboard information, and review
community recycling summaries.

## Project Information

- Module: Java II Group Assignment
- Application type: Desktop application
- Programming language: Java 8
- User interface: JavaFX
- Data storage: Plain text files
- IDE support: Eclipse and Visual Studio Code
- Build system: Manual `javac` compilation
- External libraries: None

## Main Features

### Resident Features

- Register a resident account
- Log in using a username and password
- View the resident dashboard
- Create a recycling pickup booking
- View personal booking history
- View reward points and recycling tier
- Edit resident profile information
- Log out of the application

### Administrator Features

- Log in using an administrator account
- View the administrator dashboard
- View and manage resident records
- View and update recycling bookings
- Update booking statuses
- View community recycling summaries
- Log out of the application

## Requirements

The original application requires:

- JDK 8
- JavaFX bundled with the JDK
- Eclipse or Visual Studio Code with Java support

The project was developed for Java 8. JavaFX is not bundled with standard
JDK 11 or later installations, so using JDK 8 is the simplest way to compile
and run this version.

Verify the active Java version:

```text
java -version
javac -version
