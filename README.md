# CuboidSimplePhaseNetSearch

## (NOTICE: README.md is still a work-in-progress)

## Goal:

Find a net that folds into 3 cuboids by being strategic about what to search.
Before creating this repo, I worked for months on making an algorithm that will make a thorough search of a given area of the net, and I had some success with it.
(See: https://github.com/hidny/CuboidNetSolver/tree/main and https://github.com/hidny/weirdMathStuff/tree/master/src/OneNet3Cuboids)

For example, I was able to find that there's no area 46 solutions if you disallow invisible cuts in the net.

Instead of just continuing in that direction, I figure that I might find success faster if I focus on some easy to find nets.

Once done with searching the easy to find nets, I plan to find clever ways of expanding my search to include more and more types of nets until the search becomes exhaustive.

On September 17th, I finally managed to find 'easy' nets that fold 3 cuboids, but the area of the net is a bit too big for my liking.


## The Nets that Fold 3 cuboids

I found 15 nets that fold into 3 cuboids. They are what I consider the easiest type of net. (i.e. The 'simply stacked nets')

Feel free to look at them in the 'interestingOutput\outputArea106SimpleStackingSolutions.txt' file.

The nets I found are of area 106 and Landon Kryger was able to verify that the 1st net works 8-9 hours after I shared it with him.
I searched for the 'simply stacked nets' of area 106 because I already searched for this type of net using small areas and didn't find anything.
In the future, I plan to find smaller nets by expanding my search a bit more.

An interesting fact about the 15 nets I found is that in all cases:
cell index #0 of the (8x5x1) cuboid matches up with the index #0 of the 26x1x1 cuboid.
I don't think it's a coincidence, but I still can't confidently explain why it's not a coincidence.

## State of the Code
This repo is currently very messy because I didn't delete files I stopped using, and I didn't get rid of previous versions of the same code just in case I wanted to refer back to it.

## State of README
I haven't had the time to write a thorough readme. This current iteration of it is just a skeleton of what I want it to be.


## Easy Net Targets

### The 'simple phase nets'
One type of easy to find net is the 1x1xN 'The simple phase' nets.

This is what I call the type of 1x1xN net where the bottom cell and the top cell only have 1 neighbour. The resulting nets end up looking like this:

Example 1:
|..|..|..|..|..|..|..|..|13|..|
|..|..|..|..|..|..|12| 9| 6| 3|
|..|..|..| 8| 5| 2|11|..|..|..|
| 4| 1|10| 7|..|..|..|..|..|..|
|..|..| 0|..|..|..|..|..|..|..|

Example 2:
|..|..|13|..|..|..|..|
|..|..|12| 9| 6| 3|..|
| 5|..|..| 8|..| 2|11|
| 4| 1|10| 7|..|..|..|
|..|..| 0|..|..|..|..|

(Note that in these examples, cell 0 is the bottom and cell 13 is the top)``` 

###  The 'simply stacked nets'

(Just the above example 1 but not example 2)



## Matrixification of the 1x1xN 'The simple phase' nets.

(TODO)

TODO: mention the eigenvalue of 9.49... and the significance of it...

TODO: mention that I might find other eigenvalues...



## Possible extensions

### The 'flower'

### The 'double flower'


### The whole 1x1xN space


### Reset but for 1x2xN


....



## Tricks:

### put state in 64-bit longs

### reintroduce regions

### Precompute allowed extensions to the 1x1xN cuboid, so we could go faster...


### Special long filters where there's more precomputation...


## File structure:

TODO: (As of this writing, I actually just got back to working on this repo after a 2-month break, so I'll have to figure it out...)


## Algo:


### Basic exponential search

### 'Grounded cells'

### Precompute based on top-left grounded cell...