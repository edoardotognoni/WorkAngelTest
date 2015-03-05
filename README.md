# Work Angel Test
This code is for a test that I got from a company. The test was about creating a company employees view. It has to show every employees' data like name,department,address,phone,... 
There were some mandatory requisites and other optional ones. I decided to imlpement all of them apart an optional one. I just left out this one in particular: 
>As a user I want to be able to export the directory so I can import it into a different system

###Design patterns
In this project I used the *Factory* and the *Singleton* patterns. I used both of them for my Database and Network framework. It could be useful in the future where probably we want to add a new Framework or maybe other frameworks are better for specific network or db calls.

###External Libraries
* **ORMLite (DatabaseFramework)**: I think it's a great framework. It wraps over SQLite and let you work with Beans rather then SQL queries. For a matter of clean and reusable code is definitely great.
* **Retrofit (Network Framework)**: Auto JSON conversion, great error management, great logging, and good flexibility. 
* **EventBus (Event drive messaging)**: I like the event driven development. In particular with EventBus you can fire your define events and everyone can get them in a few lines of code. I think it's really useful when managing Fragments transactions.
* **ION (UI)**: Even if it lets you do a lot of stuff, in this project I used it for bitmaps management. Bitmap caching and easy ImageViews management.
* **Crouton (UI)**: I decided to use this only because I don't like to give messages to users with bad looking Toasts or intrusive dialogs.
* **AndroidSlidingUpPanel (UI)**: It's like PlayMusic bottom panel when playing music. It's just to improve UX and UI a little bit. 

###Challenges
The real challenge of this test was this: From an Employee, we want to navigate to its subordinates and its boss. I really enjoyed solving it. I simply built a tree where each node (Employee) has a list of childs (if any) and a parent node (boss) if any.

###Conclusions
Due to the small amount of time, I focused on the main points: Clean and reasuble code with use of Design patterns with a foot in performance and memory management. I tried to build a good looking UI but I didn't want to spend a lot of time in it. 