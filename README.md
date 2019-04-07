# Replacing ButterKnife with Kotlin Synthetics

## TL;DR

Kotlin synthetics is an excellent replacement for either findViewById or
ButterKnife when accessing view elements in XML layouts.
It reduces the lines of code and does not use annotations.
Since it uses findViewById internally, it is just as performant.

However, you must be careful when using it in Fragments since
it depends on the "Fragment.onCreateView" method having been
run.  Ideally, use it in the "Fragment.onViewCreated" method 
(or any method executed after that)

This is necessary because synthetics use #Fragment.getView# to find view elements,
which is only set by the return from "Fragment.onCreateView"

In Activities, you can reference synthetics anytime after
Activity.setContentView is called.

### Usage

Layout
```xml
<TextView
  android:id="@id/myTextView"
  android:layout_height="wrap_content"
  android:layout_width="wrap_content" />
```
The name of the field in code must be the same as the id (and the same case)

Kotlin Code
```kotlin
myTextView.text = "Hello World"
```

Note:  There is no need to create a property for "myTextView"

## Getting Started

Download the sample project from

http://www.github.com/bizzguy/article-kotlin-synthetics

## Remove starter project

### Demonstrate features

### See Gradle Libraries

#### KS requires
- Kotlin language
- Kotlin extension library (this is something specific to android)


Create new project with single fragment
- create new project with no activity
– change name
– use AndroidX
- switch to project view
- new activity (Fragment + ViewModel
– launcher activity
– remove ViewModel
– enlarge textview
– add button

https://stackoverflow.com/questions/20160190/uniqueness-of-an-id-for-view-object-within-a-tree[https://stackoverflow.com/questions/20160190/uniqueness-of-an-id-for-view-object-within-a-tree]
https://stackoverflow.com/questions/18067426/are-android-view-id-supposed-to-be-unique[https://stackoverflow.com/questions/18067426/are-android-view-id-supposed-to-be-unique]

Add button and display counter

convert to dagger

* add dagger library

convert to kotlin

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
```java
@Override
public void setContentView(int resId) {
    ensureSubDecor();
    ViewGroup contentParent = (ViewGroup) mSubDecor.findViewById(android.R.id.content);
    contentParent.removeAllViews();
    LayoutInflater.from(mContext).inflate(resId, contentParent);
    mOriginalWindowCallback.onContentChanged();
}
```

### Converting Kotlin ButterKnife to Kotlin Synthetics

#### Remove BK Annotations
```
    ~~@BindView(R.id.counterText)~~
    ~~@JvmField~~
    var counterText: TextView? = null

    ~~@BindView(R.id.button)~~
    ~~@JvmField~~
    var button: Button? = null
```
#### Remove BK Binding
#### Verify that BK is being used
#### Remove properties since they are not used
#### 


## Resources

## Followup Questions
 
- How is all this effected by Android Data Binding

- Don't have to convert in some many steps.  Just go right to Kotlin Synthetics

- Alternate naming schemes for layout elements.
