# Replacing ButterKnife with Kotlin Synthetics

TLDR

Reference to any sythetic view elements must not be made in "onCreateView".  
Instead, place them in "onViewCreated" or any methods called after that.
This is necessary because synthetics use #Fragment.getView# to find view elements.


Create new project with single fragment
- create new project with no activity
-- change name
-- use AndroidX
- switch to project view
- new activity (Fragment + ViewModel
-- launcher activity
-- remove ViewModel
-- enlarge textview
-- add button

https://stackoverflow.com/questions/20160190/uniqueness-of-an-id-for-view-object-within-a-tree
https://stackoverflow.com/questions/18067426/are-android-view-id-supposed-to-be-unique


Add button and display counter

convert to dagger

- add dagger library

convert to kotlin

covert to synthetics

Comparison of BK with KS for each of these concerns
- name space
-- show what happend if use same id in "include" or id from another activity

Questions
 - AppCompatActivity or FragmentActivity
 -- AppCompatActivity extends FragmentActivity
 - why do kotlin butterknife variables default to private
 - check what a regular variable defaults to