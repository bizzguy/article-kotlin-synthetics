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

This step is just a quick reminder of how to connect XML layout elements to Java code.  This just puts us all on the same page for those who might be new to Android and gives us a bit of a deep-dive into how `findViewById` actually works. 
This will be imporant since all the binding techniques use `findViewById` under the hood.

#### Add properties to hold view references

```
TextView counterText;
Button button;
```

#### Bind properties to layout elements
```java
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
```java

public final <T extends View> T findViewById(@IdRes int id) {
    if (id == NO_ID) {
        return null;
    }
    return findViewTraversal(id);
}

/**
 * Used to mark a View that has no ID.
 */
public static final int NO_ID = -1;
```

This calls 'View.traversal'
```java
protected <T extends View> T findViewTraversal(@IdRes int id) {
    if (id == mID) {
        return (T) this;
    }
    return null;
}
```

What is the `protected` modifier for?  This is for methods with default
implementations that can be over-ridden in subtypes

Layouts have their own version of ~findViewTraversal~

'ViewGroup.traversal'
```java
@Override
protected <T extends View> T findViewTraversal(@IdRes int id) {
    if (id == mID) {
        return (T) this;
    }

    final View[] where = mChildren;
    final int len = mChildrenCount;

    for (int i = 0; i < len; i++) {
        View v = where[i];

        if ((v.mPrivateFlags & PFLAG_IS_ROOT_NAMESPACE) == 0) {
            v = v.findViewById(id);

            if (v != null) {
                return (T) v;
            }
        }
    }

    return null;
}
```

The important lines are

final View[] where = mChildren;

and 

v = v.findViewById(id);

If the `ViewGroup` itself is not the view being looked for, 
then search through all the children.

The first view or child with the id is selected.


# Step 2 - Replace `View#findViewById` with ButterKnife

<<<<<<< HEAD
#### Assign each view property to its corresponding layout element

The ButterKnife annotation `@BindView` tells it which XML element
the property should be associated with.

```
    @BindView(R.id.text_counter)
    TextView counterText;

    @BindView(R.id.button)
    Button button;
```

@BindView fields must not be private or static

#### Tell ButterKnife to bind the layout elements and the view properties

BK needs to be called to perform the binding.  In this case,
"binding" refers to the process of wiring the Java property
to the XML layout element.

```
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        ButterKnife.bind(this, view);
```

#### Review the code behind the binding

The BK annotation generate some hidden code to perform the binding.

Look in the directory

  /app/build/generated/source/kapt/debug/com/article/kotlinsynthetics/b_java_butterknife
  
Now examine the file

  MainFragment_ViewBinding
  
Note.  BK names the binding file after the annotated class
by using the class name appended with "_ViewBinding"

```
    target.counterText = Utils.findRequiredViewAsType(source, R.id.text_counter, "field 'counterText'", TextView.class);
    target.button = Utils.findRequiredViewAsType(source, R.id.button, "field 'button'", Button.class);
```

This is the code to perform the binding

```
  public static View findRequiredView(View source, @IdRes int id, String who) {
    View view = source.findViewById(id);
    if (view != null) {
      return view;
    }
    String name = getResourceEntryName(source, id);
    throw new IllegalStateException("Required view '"
        + name
        + "' with ID "
        + id
        + " for "
        + who
        + " was not found. If this view is optional add '@Nullable' (fields) or '@Optional'"
        + " (methods) annotation.");
  }
```

# Step 3 - Convert to Kotlin

#### Run the Kotlin Converter Tool

Select Java file

Menu -> Code -> Convert Java File to Kotlin File

```
@BindView(R.id.countText)
internal var countText: TextView? = null

@BindView(R.id.button)
internal var button: Button? = null
```

Kotlin has assigned an `internal` modifier to the properties.

What is this for.  Think of it as "module private".  The properties
are visible anywhere within the module.

#### Examine Generated Kotlin Code

Menu -> Tools -> Kotlin -> Show Kotlin Bytecode

Once the bytecode as been generated, select "Decompile" to turn the byte
code into a readable form in Java (this is just a temporary file).

```
@BindView(-1000031)
@Nullable
private TextView countText;

@BindView(-1000090)
@Nullable
private Button button;
```

Remember, @BindView doesn't like privates so we get a compiler error.

Since we've already removed "private" what do we do?

Use the special Kotlin annotation @JvmField

```kotlin
@BindView(R.id.counterText)
@JvmField
var counterText: TextView? = null

@BindView(R.id.button)
@JvmField
var button: Button? = null
```

What does JvmField do?  

From the doc:

Instructs the Kotlin compiler not to generate getters/setters for this property and expose it as a field.

See the [Kotlin language documentation](https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html#instance-fields) for more information.

The field will have the same visibility as the underlying property. 

=======
Let's use the ButterKnife library to simplify the binding. 

Since BK uses annotations, we can explore the generated code to understand how BK actually binds the view elements.

We'll discover that it also uses findViewById.

# Step 3 - Convert to Kotlin

We'll convert the Java code to Kotlin just to emphasize the idea that any Java code (and any annotations) can also 
run in Kotlin. 
>>>>>>> 07780557bb73ae935eaf1403542a1f281619411d

# Step 4 - Replace Butterknife with Kotlin Synthetics

Now we'll actually replace ButterKnife with Kotlin Synthetics.

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
| Scope | Broadest - can pick ids from any layout in app|same as FV | Narrower - can only pick ids from imported layouts|
| Number of Lines |Property and wiring | Most - annotation, property and binding | Least - no wiring lines of code|
| Performance | | | |
| Building | | | |


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

Find my children

Wiring vs Data Binding

Wiring is the technique of associated views defined in layouts with
the Java or Kotlin code that controls them

Data binding goes one step further
- wiring
- assign data values and handle changes in data values
from either the screen or other sources
