# Description

The application is created for making call/text someone quicker and easier.

Require these permissions :

1. android.permission.CALL_PHONE, so that it can directly call the one when you click the notification.
2. android.permission.READ_CONTACTS, so that it can read the phone number of the one you select in contacts.
3. android.permission.RECEIVE_BOOT_COMPLETED, so that it can make sure after your reboot your notification still show up.
4. android.permission.WAKE_LOCK, so that JobIntentService can work on prior-Oreo Android. [Related issue](https://github.com/Shawlaw/CallInTwo/issues/2).

All icons are downloaded from [Material Icons](https://material.io/icons/), and I just slightly change the color of some icons.



### Screenshots

#### Empty List

<img src="./readmeImgs/emptyList.jpg" width="360px" height="640px" alt="Empty list"/>

#### Contact Detail

<img src="./readmeImgs/contactDetail.jpg" width="360px" height="640px" alt="Contact Detail"/>

#### Normal List

<img src="./readmeImgs/normalList.jpg" width="360px" height="640px" alt="Normal list"/>

#### Unchecked Notification

<img src="./readmeImgs/uncheckedNotification.jpg" width="360px" height="640px" alt="Unchecked Notification"/>

#### Both checked Notification

<img src="./readmeImgs/bothCheckedNotification.jpg" width="360px" height="640px" alt="Both checked Notification"/>
