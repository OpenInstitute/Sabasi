# Sabasi
Sabasi is a swahili word meaning "Gossip". Sabasi is an Android app for filling out forms.  It is designed to be used in resource-constrained environments with challenges such as unreliable connectivity or power infrastructure. One of the main features is the ability for the user to connect to a server of their choice and have the data stored and retrieved without need of third party access, giving the user full control of the whole process. It is a free and open-source tool which help organizations author, field, and manage mobile data collection solutions. 

Sabasi will have a web interface with similar features, but includes a dashboard and download features. It will allow multiple users with different security rights.


## Setting up your development environment

1. Download and install [Android Studio](https://developer.android.com/studio/index.html) 

1. Fork the Sabasi project ([why and how to fork](https://help.github.com/articles/fork-a-repo/))

1. Clone your fork of the project locally. At the command line:

        git clone https://github.com/OpenInstitute/Sabasi.git

 If you prefer not to use the command line, you can use Android Studio to create a new project from version control using `https://github.com/OpenInstitute/Sabasi.git`. 

1. Open the project in the folder of your clone from Android Studio. To run the project, click on the green arrow at the top of the screen. The emulator is very slow so we generally recommend using a physical device when possible.


## Contributing code
Any and all contributions to the project are welcome. Sabasi has been used in the African region by organisations targeting social purpose so you can have real impact! (http://lanet.opencounty.org/).

If you're ready to contribute code, see [the contribution guide](CONTRIBUTING.md).

## Contributing in other ways

You can also help by improving this documentation.


## Troubleshooting
#### Android Studio Error: `SDK location not found. Define location with sdk.dir in the local.properties file or with an ANDROID_HOME environment variable.`
When cloning the project from Android Studio, click "No" when prompted to open the `build.gradle` file and then open project.
