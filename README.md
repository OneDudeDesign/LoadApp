# LoadApp
Android-Kotlin Nanodegree project 3

Regarding permissions, I hardcoded the Network and Internet permissions because without them well there is no app, I suspect a user installing this 
app to download files would understand it needs network to do so, if I give them the ability to deny permissions the app is useless.

Attributions:
Documentation reviewed from:
https://developer.android.com/reference/android/app/DownloadManager.Request?authuser=1#setDestinationUri(android.net.Uri)

figured out I needed to use a cursor for the downloadmanger.query reading: 
https://stackoverflow.com/questions/26210048/how-to-receive-status-of-download-manager-intent-until-download-success-or-faile
--my code is different but gave me a starting point

figured out use of timer by reading 
https://code.luasoftware.com/tutorials/android/android-download-file-with-downloadmanager-and-check-status/
and converting it to kotlin looking a developer.android.com

checking for network mode adapted from https://www.tutorialspoint.com/how-to-check-internet-connection-availability-and-the-network-type-on-android-using-kotlin

value animation adapted from http://raphaelfavero.github.io/Creating_Animated_Custom_View/

text centering https://blog.danlew.net/2013/10/03/centering_single_line_text_in_a_canvas/

Graphics:
all graphics and icons not provided by android studio were created by me in Adobe AI