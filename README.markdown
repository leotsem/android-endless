CWAC EndlessAdapter: It Just Keeps Going and Going And...
=========================================================

AJAX Web sites have sometimes taken up the "endless page"
model, where scrolling automatically loads in new content,
so you never have to click a "Next" link or anything like that.

Wouldn't it be cool to have that in an Android application?
Kinda like how the Android Market does it?

`EndlessAdapter` is one approach to solving this problem.

It is designed to wrap around another adapter, where you have
your "real" data. Hence, it follows the Decorator pattern,
augmenting your current adapter with new Endless Technology(TM).

To use it, you extend `EndlessAdapter` to provide details about
how to handle the endlessness. Specifically, you need to be
able to provide a row `View`, independent from any of the rows
in your actual adapter, that will serve as a placeholder
while you, in another method, load in the actual data to
your main adapter. Then, with a little help from you, it
seamlessly transitions in the new data.

So, this is not truly "endless" insofar as the user does see
when we load in new data. However, it should work well for
Android applications backed by Web services or the like
that work on "page-at-a-time" metaphors -- users get the
additional data quickly and do not incur the bandwidth to
download that data until and unless they scroll all the
way to the bottom.

Note that this has been tested with `ArrayAdapter` extensively
but may not work with other adapter types, particularly
`SimpleAdapter`.

This is available as a JAR file from the downloads area of this GitHub repo.
The project itself is set up as an Android library project,
in case you wish to use the source code in that fashion.

Usage
-----
To use `EndlessAdapter`, you need to create a subclass that
will control the endlessness, specifying what `View` to use
for the "loading" placeholder, and then updating that placeholder
with an actual row once data has been loaded.

`EndlessAdapter` assumes there is at least one more "batch" of
data to be fetched. If everything was retrieved for your
`ListAdapter` the first time out (e.g., the Web search returned
only one "page" of results), do not wrap it in `EndlessAdapter`,
and your users will not perceive a difference.

### Constructors

`EndlessAdapter` has two constructors. The original one takes a `ListAdapter` as
a parameter, representing the existing adapter to be made
endless. Your `EndlessAdapter` subclass will need to override
this constructor and chain upwards. For example, the DemoAdapter
inside the demo project takes an `ArrayList<String>` as a
constructor parameter and wraps it in a `ListAdapter` to supply
to `EndlessAdapter`.

The second constructor takes a `Context` and resource ID along with
the `ListAdapter`. These will be used to create the placeholder
(see below).

### The Placeholder

Your `EndlessAdapter` subclass can implement `getPendingView()`.
This method works a bit like the traditional `getView()`, in that
it receives a `ViewGroup` parameter and is supposed to return a
row `View`. The major difference is that this method needs to
return a row `View` that can serve as a placeholder, indicating
to the user that you are fetching more data in the background
(see below). This `View` is not cached by `EndlessAdapter`, so
if you wish to reuse it, cache it yourself.

If you use the constructor that takes a `Context` and resource ID along with
the `ListAdapter`, you can skip `getPendingView()`, and `EndlessAdapter`
will inflate the supplied layout resource as needed to create
this placeholder.

### The Loading

Your `EndlessAdapter` subclass also needs to implement `cacheInBackground()`.
This method will be called from a background thread, and it needs
to download more data that will eventually be added to the `ListAdapter`
you used in the constructor. While the demo application simply sleeps for 10 seconds, a real
application might make a Web service call or otherwise load in
more data.

This method returns a `boolean`, which needs to be `true` if there
is more data yet to be fetched, `false` otherwise.

Since this method is called on a background thread, you do not
need to fork your own thread. However, at the same time, do not
try to update the UI directly.

If you expected to be able to retrieve data, but failed (e.g., network
error), that is fine. However, you should then return `false`, indicating
that you have no more data.

### The Attaching

Your `EndlessAdapter` subclass also needs to implement `appendCachedData()`,
which should take the data cached by `cacheInBackground()` and append
it to the `ListAdapter` you used in the constructor. While
`cacheInBackground()` is called on a background thread,
`appendCachedData()` is called on the main application thread.

If you had a network error in `cacheInBackground()`, simply do nothing
in `appendCachedData()`. So long as you returned `false` from
`cacheInBackground()`, `EndlessAdapter` will remove the placeholder
`View` and will operate as a normal fixed-length list. Or,
override `onException()` to get control on the main application
thread and be passed the `Exception` raised by `cacheInBackground()`,
so you can do something to let the user know what went wrong.
Have `onException()` return `true` if you want to retry loading data in the background,
`false` otherwise.

Dependencies
------------
This project relies upon the [CWAC AdapterWrapper][adapter] project.
A copy of compatible JARs can be found in the `libs/` directory
of the project, though you are welcome to try newer ones, or
ones that you have patched yourself.

Version
-------
This is version v0.7.0 of this module, meaning it is slowly
becoming a respected member of the Android community.

Demo
----
In the `demo/` sub-project you will find
a sample activity that demonstrates the use of `EndlessAdapter`.

Note that when you build the JAR via `ant jar`, the sample
activity is not included, nor any resources -- only the
compiled classes for the actual library are put into the JAR.

License
-------
The code in this project is licensed under the Apache
Software License 2.0, per the terms of the included LICENSE
file.

Questions
---------
If you have questions regarding the use of this code, please post a question
on [StackOverflow](http://stackoverflow.com/questions/ask) tagged with `commonsware` and `android`. Be sure to indicate
what CWAC module you are having issues with, and be sure to include source code 
and stack traces if you are encountering crashes.

Release Notes
-------------
* v0.7.0: `cacheInBackground()` can now throw checked exceptions, new `getContext()` method available for subclasses
* v0.6.1: merged bug fix from rgladwell/cwac-endless; added @Override annotations
* v0.6.0: added pending `View` support via constructor
* v0.5.0: added `onException()`
* v0.4.0: eliminated need for `rebindPendingView()`, documented the no-data scenario
* v0.3.1: fixed bug in manifest
* v0.3.0: converted to Android library project, added call to `notifyDataSetChanged()`

[gg]: http://groups.google.com/group/cw-android
[adapter]: http://github.com/commonsguy/cwac-adapter/tree/master