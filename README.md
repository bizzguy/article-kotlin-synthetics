# Replacing findViewById or ButterKnife with Kotlin Synthetics in Android Apps

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

#### Kotlin Code

Just reference the element with the same name as in the layout.  There is no need to create an instance property.

```.java
myTextView.text = "Hello World"
```

#### Libraries and Plugins

Include the Kotlin Extensions in your project.  It will be included automatically when you create a new project with the Android Studion wizard.  There is no need to add any additional libraries.

File: `app/build.gradle`
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
public final <T extends View> T findViewById(@IdRes int id) {
    if (id == NO_ID) {
        return null;
    }
    return findViewTraversal(id);
}
```

The method 'View.findViewTraversal'

```.java
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

`ViewGroup.findViewTraversal`

```.java
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

```
final View[] where = mChildren;
```

and 

```
v = v.findViewById(id);
```

If the `ViewGroup` itself is not the view being looked for, 
then search through all the children.  The first view or child with the id is selected.


# Step 2 - Replace `View#findViewById` with ButterKnife

Although BK has other capabilities, such as wiring `onClick` listeners, its primary
purpose is to replace `findViewById`

#### Assign each view property to its corresponding layout element

The ButterKnife annotation `@BindView` tells BK which XML element
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

The `@BindView` annotations causes BK to create some hidden generated code.

BK names the binding file after the annotated class by using the class name appended with "_ViewBinding" 
  
Now examine the file

Look in the directory

  /app/build/generated/source/kapt/debug/com/article/kotlinsynthetics/b_java_butterknife
  
  MainFragment_ViewBinding 
  
  Here's the constructor.

```
@UiThread
public MainFragment_ViewBinding(MainFragment target, View source) {
    this.target = target;
    target.counterText = Utils.findRequiredViewAsType(source, R.id.countText, "field 'counterText'", TextView.class);
    target.button = Utils.findOptionalViewAsType(source, R.id.button, "field 'button'", Button.class);
}
```

There is a line for each property to be bound to a layout element.
Notice the constructor does not return any views, it initializes the property in the original class.

We've got the reference to the property.  BK now finds the reference to the view.
It does this in two steps.  
First call `findRequiredViewAsType` which will return the reference as the 
correct high level type


butterknife.internal\Utils
```
  public static <T> T findRequiredViewAsType(View source, @IdRes int id, String who,
      Class<T> cls) {
    View view = findRequiredView(source, id, who);
    return castView(view, id, who, cls);
  }
```

Second by finding the view (as View type which must be recast)

This is the code to find the view.

```
  public static View findRequiredView(View source, @IdRes int id, String who) {
    View view = source.findViewById(id);
    if (view != null) {
      return view;
    }  + " (methods) annotation.");
    ...
  }
```

Underneath it all, BK is just performing a `findViewById`

How is the binding initialized:

    ButterKnife.bind(this, view)
    
    public static Unbinder bind
    
    Constructor<? extends Unbinder> constructor = findBindingConstructorForClass(targetClass);

    Class<?> bindingClass = cls.getClassLoader().loadClass(clsName + "_ViewBinding");
    
    constructor.newInstance(target, source);
    
Once `ButterKnife#bind` is run, the properties are initialized with references to the XML
view elements.

java.lang.reflect.Constructor uses reflection.  Will this be a performance issue?   

# Step 3 - Convert to Kotlin

We'll convert the Java code to Kotlin just to demonstrate that any Java code (and any annotations) can also 
run in Kotlin.  This is an option step - we could remove ButterKnife and convert to Kotlin in a single step.

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

Causes the field to have the same visibility as the underlying property. 

See the [Kotlin language documentation](https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html#instance-fields) for more information.




# Step 4 - Replace Butterknife with Kotlin Synthetics

Now let's replace ButterKnife with Kotlin Synthetics.

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

#### Remove properties since they are not used

```
    //var counterText: TextView? = null

    //var button: Button? = null
```

#### Run the app and look for failures

Examine logcat to see the error


null pointer exception

fix by moving code to onCreateView

```.java
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    button!!.setOnClickListener {
        counter++
        countText!!.text = counter.toString()
    }
}
```

Re-run the app, it should work

#### See how KS is working

See import statements

```.java
import kotlinx.android.synthetic.main.main_fragment.*
```

Also notice the all the BK import statements are gone.

##### How does KS reference the view elements.

Show Kotlin Bytecode and Decompile back to java

```.java
public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
  Intrinsics.checkParameterIsNotNull(view, "view");
  Button var10000 = (Button)this._$_findCachedViewById(id.button);
  if (var10000 == null) {
     Intrinsics.throwNpe();
  }

  var10000.setOnClickListener((OnClickListener)(new OnClickListener() {
     public final void onClick(View it) {
        MainFragment.this.counter = MainFragment.this.counter + 1;
        TextView var10000 = (TextView)MainFragment.this._$_findCachedViewById(id.countText);
        if (var10000 == null) {
           Intrinsics.throwNpe();
        }

        var10000.setText((CharSequence)String.valueOf(MainFragment.this.counter));
     }
  }));
}
```

```
Button var10000 = (Button)this._$_findCachedViewById(id.button);
```

Very strangely named method.  Using understore or $ to begin a
method name is a typical way of avoiding collisions with regular
methods

_$_findCachedViewById
```
   public View _$_findCachedViewById(int var1) {
      ...

      View var2 = (View)this._$_findViewCache.get(var1);
      if (var2 == null) {
         ...
         var2 = var10000.findViewById(var1);
         this._$_findViewCache.put(var1, var2);
      }

      return var2;
   }
```

#### The algorithm for the `findCachedViewById`

- If cache storage is null then create it
- Check cache storage for reference to view
- If not in cache then look up reference using `findViewById`
- If reference not in cache then add it to cache

```
private HashMap _$_findViewCache;
```

### Why did KS fail at first
```
View var10000 = this.getView();
```


Fragment#getView
```
@Nullable
public View getView() {
    return mView;
}
```

Fragment#performCreateView
```
 mView = onCreateView(inflater, container, savedInstanceState);
``` 

Can't run `Fragment#getView` until after `onCreateView` is run.      


#### A little more cleanup

Remove the !! code on the references
```
button!!.setOnClickListener
```
becomes
```
button.setOnClickListener
```

And now we're done!



# Conclusion

Kotlin Synthetics are an excellent solution for wiring XML layout elements to their controlling fragment or activity.

## Comparison

| Feature | Java findViewById | ButterKnife | Kotlin Synthetics |
| --- | --- | --- | --- |
| Scope | Broadest - can pick ids from any layout in app|same as FV | Narrower - can only pick ids from imported layouts|
| Number of Lines |Code for Property and wiring | Code for annotation, property and binding (most) | No code for wiring (least) |
| Performance | Uses findViewById | Uses findViewById - Also uses reflection to perform binding | Uses findViewById |
| Building | Minimal | Annotation processing | Minimal |
| Recommended by Jake Wharton | No | He wrote ButterKnife | He is now the Kotlin Ambassador at Google |



Read the Code!

[https://blog.codinghorror.com/learn-to-read-the-source-luke/]




## Followup Questions

#### Could Android have cached the result of findViewById?

#### Don't have to convert in some many steps.  Just go right to Kotlin Synthetics

#### Alternate naming schemes for layout elements


#### Difference between Wiring and Data Binding

Wiring is the technique of associated views defined in layouts with
the Java or Kotlin code that controls them

Data binding goes one step further
- wiring
- assign data values and handle changes in data values
from either the screen or other sources

#### How is all this effected by Android Data Binding

#### Why do both of these compile:

```
   counterText.setText(Integer.toString(counter));
   counterText.setText(counter);
```
   
But one of the above fails at runtime (when executing)   
   
#### Why does this fail to compile:
   
   counterText.text = counter

#### In fragments, why does this fail:
  
   ButterKnife.bind(activity!!, view)
   
but this succeeds

   ButterKnife.bind(this, view)  
   
   
#### What is the scope of findView
- getView
- set in fragment
- whatever is returned by the onCreateView
- which is the layout
- can there be two layouts (other)
- yes, but you'll see it in the imports
- often it is an adapter layout which could be moved to adapter
- create link check to very that there are not two synthetic imports
-- this happens alot because of copy and paste

#### How does Activity set content?

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

#### What data type is field when using KS

#### What happens if the same id is used in "include" (or id from another fragment)

#### When is wiring performed
 - BK - all fields at start up
 - KS - only fields that are actually used, when they are used name space
 - FV - all layouts
 - BK - only inflated view
 - KS - only inflated view
 
#### AppCompatActivity or FragmentActivity

#### AppCompatActivity extends FragmentActivity

#### why do kotlin butterknife variables default to private

#### check what a regular variable defaults to

#### what does "kapt" mean (acronym?)



## Resources

Reach me at: [jamesharmon@gmail.com]

LinkedIn: [https://www.linkedin.com/in/jamesharmonandroid/]

Useful article about performance trade-offs

[https://proandroiddev.com/kotlin-android-synthetics-performance-analysis-with-butterknife-90a54ca4325d]

Kotlin properties are private by default (with getters/setters)

[https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html]


Text-based Role Playing Game:

[https://en.wikipedia.org/wiki/Colossal_Cave_Adventure]
