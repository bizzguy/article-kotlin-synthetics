# Replacing ButterKnife with Kotlin Synthetics in Android Apps

## TL;DR

Kotlin synthetics is an excellent replacement for either findViewById or
ButterKnife when accessing view elements in XML layouts.
It reduces the lines of code, avoids annotations and provides some scoping.
Since it uses `findViewById` internally, it is just as performant as the other two techniques.

But there are some gotchas.  You must be careful when using synthetics in Fragments since
they depend on the `Fragment#onCreateView` method having been
run.  Ideally, use them in the `Fragment#onViewCreated` method called after that.

In Activities, you can reference synthetics anytime after
`Activity#setContentView` is called.

### Using Kotlin Synthetics

#### Layout
Nothing special must be done to existing layouts.  Use them as they are.

```xml
<TextView
  android:id="@id/myTextView"
  android:layout_height="wrap_content"
  android:layout_width="wrap_content" />
```
The name of the field in code must be the same as the id (and the same case)

#### Kotlin Code

Just reference the element with the same name as in the layout.  There is no need to create an instance property.

```kotlin
myTextView.text = "Hello World"
```

#### Libraries and Plugins

Just include the Kotlin Extensions in your project.  It will be included automatically when you create a new project with the Android Studion wizard.  There is no need to add any additional libraries.

app/build.gradle
```
apply plugin: 'kotlin-android-extensions'
```

## Getting Started Code

Download the sample project from

    http://www.github.com/bizzguy/article-kotlin-synthetics

### Walkthrough the Code

#### Sample App Features

This sample app displays a screen with a counter starting at 0 and a button which will increment the counter when pressed.

<< screen shot >>

#### Review Gradle build files 

- Kotlin language
- Kotlin extension library (this is something specific to android)

This has the gradle files for kotlin and Butterknife.

Project uses AndroidX

#### Review Layout

Synthetics requires no changes to layouts.

#### Review Starting Code

Activity will create a gragment.  The fragment will control the screen.


# Step 1 - Bind layout elements to Java properties using `View#findViewById`

#### Add properties to hold view references

```java
TextView counterText;
Button button;
```

#### Bind properties to layout elements
```
countText = view.findViewById(R.id.countText)
button = view.findViewById(R.id.button)
```

#### Add functionality to properties
```
counterText.setText(Integer.toString(counter));

button.setOnClickListener(v -> {
    counter++;
    counterText.setText(Integer.toString(counter));
});
```

#### Explore findViewById

Let's drill down into the code for `findViewById` and see what is actually being executed.

The first call is to `View.findViewById`
```
```

This calls 'View.traversal'
```
```

View traversal is over-ridden by ViewGroup
- ViewGroup override of traversal

# Step 2 - Replace `View#findViewById` with ButterKnife

# Step 3 - Convert to Kotlin

# Step 4 - Replace Butterknife with Kotlin Synthetics

#### Remove BK Annotations
```kotlin
    //@BindView(R.id.counterText)
    //@JvmField
    var counterText: TextView? = null

    //@BindView(R.id.button)
    //@JvmField
    var button: Button? = null
```

#### Remove BK Binding
```
     //ButterKnife.bind(this, view)
```

#### Verify that BK is being used

#### Remove properties since they are not used
```
    //var counterText: TextView? = null

    //var button: Button? = null
```

#### Verify that KS is now being used

See import statements

See code for __

Hash Map
Wierdly named method
Calls findViewById and caches results

Which findView is running
- View
- Traversal
- ViewGroup override of traversal

Could Android have cached the result of findViewById


covert to synthetics

Comparison of BK with KS for each of these concerns
- name space
– show what happend if use same id in "include" or id from another activity

Questions
 - AppCompatActivity or FragmentActivity
 – AppCompatActivity extends FragmentActivity
 - why do kotlin butterknife variables default to private
 - check what a regular variable defaults to
 - what does "kapt" mean (acronym?)
 - delete "build" directory before starting talk
 - refresh MainFragment before starting talk

* Kotlin properties are private by default (with getters/setters)
 – https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html[https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html]

when is binding done (performance)
 - BK - all fields at start up
 - KS - only fields that are actually used, when they are used
 name space
 - FV - all layouts
 - BK - only inflated view
 - KS - only inflated view

* only have one layout
* adapters could have layout encapsulated within them

KS
 - imports fields in layout at compile time
 - creates hidden caching function that uses findViewById

what data type is field when using KS

very useful article about performance trade-offs
 https://proandroiddev.com/kotlin-android-synthetics-performance-analysis-with-butterknife-90a54ca4325d[https://proandroiddev.com/kotlin-android-synthetics-performance-analysis-with-butterknife-90a54ca4325d]

medium - include stackoverflow link and github/gist links

* check what modiefier a regular variable defaults to (ie private, val/var)

Create a matrix of features to compare
 - lines of code
 - name space
 - caching / performance
 - use of annotations (might slow performance)
 - recommended by Jake Wharton yes/no

Github Markdown Info
https://guides.github.com/features/mastering-markdown/[https://guides.github.com/features/mastering-markdown/]


How does activity set content?

AppCompatDelegateImpl.java#465
```
@Override
public void setContentView(int resId) {
    ensureSubDecor();
    ViewGroup contentParent = (ViewGroup) mSubDecor.findViewById(android.R.id.content);
    contentParent.removeAllViews();
    LayoutInflater.from(mContext).inflate(resId, contentParent);
    mOriginalWindowCallback.onContentChanged();
}
```


What is the scope of findView
- getView
- set in fragment
- whatever is returned by the onCreateView
- which is the layout
- can there be two layouts (other)
- yes, but you'll see it in the imports
- often it is an adapter layout which could be moved to adapter
- create link check to very that there are not two synthetic imports
-- this happens alot because of copy and paste


## Followup Questions
 
- How is all this effected by Android Data Binding

- Don't have to convert in some many steps.  Just go right to Kotlin Synthetics

- Alternate naming schemes for layout elements.

# Conclusion

## Comparison

| Feature | Java findViewById | ButterKnife | Kotlin Synthetics |
| --- | --- | --- | --- |
| Scope | | | |
| Number of Lines | | | |
| Performance | | | |
| Other |sd f  sf sd fs f sf s fs fs f sf s fs s f s s f | s d fs  s sd fsfsfsdf sd fs df sdf s df sd fs  sf| s d fs  s sd fsfsfsdf sd fs df sdf s df sd fs  sf |




<table>
  
<tr>
  <th width="16%">Feature</th>
  <th width="28%">Android findViewById</th>
  <th width="28%">ButterKnife</th>
  <th width="28%">Kotlin Synthetics</th>
</tr>

<tr>
  <td>Number of Lines</td> 
  <td>sd f  sf sd fs f sf s fs fs f sf s fs s f s s f </td>
  <td>s d fs  s sd fsfsfsdf sd fs df sdf s df sd fs  sf </td>
  <td>s d fs  s sd fsfsfsdf sd fs df sdf s df sd fs  sf </td>
</tr>

</table>




## Resources

- https://stackoverflow.com/questions/20160190/uniqueness-of-an-id-for-view-object-within-a-tree[https://stackoverflow.com/questions/20160190/uniqueness-of-an-id-for-view-object-within-a-tree]

- https://stackoverflow.com/questions/18067426/are-android-view-id-supposed-to-be-unique[https://stackoverflow.com/questions/18067426/are-android-view-id-supposed-to-be-unique]

- https://medium.com/learning-new-stuff/tips-for-writing-medium-articles-df8d7c7b33bf

Text-based Role Playing Game

    [https://en.wikipedia.org/wiki/Colossal_Cave_Adventure]

Read the Code!

    [https://blog.codinghorror.com/learn-to-read-the-source-luke/]


[https://www.brandonbloom.name/blog/2012/04/16/learn-to-read-the-source-luke/]

Text based adventure games
Professor - don't read code is same as can't read code
Design Patterns