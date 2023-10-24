
# CuboidSimplePhaseNetSearch


## Goal


Find a net that folds into 3 cuboids by being strategic about what to search.

I created this repo because I figured that I might find success faster if I focus on some easy to find nets instead of continuing with doing an exhaustive search.


Once done with searching the easy to find nets, I plan to find clever ways of expanding my search to include more and more types of nets until the search becomes exhaustive.

## Related links

Here's a link to a video that explains the challenge:

https://www.youtube.com/watch?v=jOTTZtVPrgo

Here's a link to documentation about my previous attempt to find a net that folds into 3 cuboids by exhaustive search:

https://github.com/hidny/CuboidNetSolver/tree/main/docs



## Latest update(s)

On September 17th, I finally managed to find 15 'easy' nets that fold into 3 cuboids, but the area of the nets are a bit too big for my liking.

  

 
## The Nets that Fold 3 cuboids


I managed to find 15 nets that fold into 3 cuboids. They are what I consider the easiest type of net. (i.e. The 'simply stacked nets')


Feel free to look at them in the file named: 'interestingOutput\outputArea106SimpleStackingSolutions.txt'.

  

The nets I found are of area 106 and Landon Kryger was able to quickly verify that the 1st net works after I shared it with him.

I searched for the 'simply stacked nets' of area 106 because I already searched for this type of net using smaller areas and didn't find anything.

In the future, I plan to find smaller nets by changing the category of net that I'll let the program search for.

  

An interesting fact about the 15 nets I found is that, in all cases, cell index #0 of the (8x5x1) cuboid matches up with the index #0 of the 26x1x1 cuboid.
I don't think it's a coincidence, but I still can't confidently explain why it's not a coincidence.
I also found that there were a lot of ways to have a net that covers both the 26x1x1 cuboid and the 8x5x1 cuboid (more than 9 million simply stacked nets!). I don't know why
they paired up so well, but I have the feeling there's an answer to that question.


  

## State of the Code

This repo is currently very messy because I didn't delete files I stopped using, and I didn't get rid of previous versions of the same code just in case I wanted to refer back to it.

I'll try to clean it up a bit soon.


## Easy Net Targets

  

Here's a list of the different types of nets I'm currently thinking of searching for. So far, I only searched the 'simply stacked nets'.

They are all based around the 1x1xN cuboid because I believe that's the easiest type of cuboid to deal with.

  

  

### The 'simple phase nets'

One type of easy to find net is the 1x1xN 'The simple phase' nets.

  

This is what I call the type of 1x1xN net where the bottom cell and the top cell only have 1 neighbour. The resulting nets end up looking like this:

  

Example 1:

  

|..|..|..|..|..|..|..|..|13|..|<br>

|..|..|..|..|..|..|12|#9|#6|#3|<br>

|..|..|..|#8|#5|#2|11|..|..|..|<br>

|#4|#1|10|#7|..|..|..|..|..|..|<br>

|..|..|#0|..|..|..|..|..|..|..|<br>

  

Example 2:

|..|..|13|..|..|..|..|<br>

|..|..|12|#9|#6|#3|..|<br>

|#5|..|..|#8|..|#2|11|<br>

|#4|#1|10|#7|..|..|..|<br>

|..|..|#0|..|..|..|..|<br>

  

Example 3:

|..|..|13|..|..|..|..|<br>

|..|..|12|29|26|23|..|<br>

|#5|..|11|28|..|22|..|<br>

|#4|#1|10|27|..|..|..|<br>

|..|..|#0|..|..|..|..|<br>

  

See Appendix A for the theory behind the shortcuts I'm planning on using.

  

  

### The 'simply stacked nets'

  

This is just 'simple phase nets', but every in-between layer has 4 horizontal 

cells in a row without any space in between them... (Just like example 1 of the 'simple phase nets' examples)

  

Because this is really simple, it allowed me to look at cuboids of dimension up to 1x1x26, and allowed me to find the 15 nets of area 106 that fold into 3 cuboids.

  

### The 'flower'

  

Let the bottom of the 1x1xN cuboid have multiple neighbours. I like to think of it as the bottom cell 'blooming' like a flower.

  

### The 'double flower'

  

Let the both the bottom and the top cells of the 1x1xN cuboid have multiple neighbours,

but maybe don't allow layers to have empty spaces in between unless they are connected to the top or bottom cell in different ways.

  

### The whole 1x1xN space

  

Just cover the whole space. I'm hoping to pull this off while pre-computing the layering rules for the 1x1xN cuboid.

  

  
### Rotationally symmetric solutions only

After noticing that 2 of the 15 nets that fold into 3 cuboids are rotationally symmetric, I figured that this might be a good avenue of attack.
I read about this strategy in section 3.5.1 of "Introduction to Computational Origami" by Ryuhei Uehara, but I forgot about it until now.
  

### Reset but for 1x2xN


Once 1x1xN is done and optimized, I might move on to doing the same thing for the 1x2xN cuboids...

This might not happen any time soon.

  

## Tricks to get the computation done faster:

  

### Put state in 64-bit integers

I decided to hold the state of which cuboid cells are used or not used in 64-bit integers.

This allows me to quickly do basic checks with some bit manipulation.

For example:

The question: 'Can I add layer type 0 on top of index cell 13 with rotation 1?'

becomes a question of ANDing 2 or 3 64-bit integer states.

(i.e: state_of_cells AND bit_mask_cells_to_add_in_layer)

because I can hold all the cuboid cells in 2 64-integers, that becomes only 2 AND operations.

  

### Check for region splits

  

For the 'simply stacked nets' search, I got it to go faster by checking if the unused cells of a cuboid get split into 2 regions.

If the split happens for 'simply stacked nets', that means that it just doesn't work.

I used bit-masking to make this check even faster.


  

## File structure:

  

TODO: (As of this writing, I actually just got back to working on this repo after a 2-month break, so I'll have to figure it out and I'll probably reorganize it later...)

  

  

## Algo:


### Basic DSF search

It's just a depth-first-search where the algo makes the Nx1x1 one layer at a time.

### Pre-compute based on top-left grounded cell...

To make it as fast as possible, I pre-computed as much as possible, so the depth-first-search algorithm will need to do as little as possible.

### Explanation of 'Grounded cells'

Cells that have a path to the bottom cell

Example:
15, 34, and 13 are grounded because it's connected to the 0 cell, but 34 is not:

|34|13|..|<br>
|15|..|34|<br>
|00|..|..|<br>

  

## Appendix A: 'simple phase nets' conjectures


  

I currently have three conjectures that, if true, would make the search a lot easier.

I'm a bit embarrassed about the fact that I haven't proved any of these conjectures, but I completely believe them and

I'm willing to work on this problem as if these conjectures are simply true.

The three conjectures build on each other, and I don't technically depend on the third one being true.

  

  

'simple phase nets' conjecture 1:

There's only 4 ways for a layer of an Nx1x1 cuboid to be configured:

Here they are:

{1, 1, 1, 1, 0, 0, 0}, or<br>

{1, 1, 0, 1, 0, 0, 1}, or<br>

{1, 0, 1, 1, 0, 1, 0}, or<br>

{1, 0, 0, 1, 0, 1, 1}<br>

  

Example option 1:

|24|21|10|27|<br>

  

Example option 2:

|25|22|..|28|..|..|11|<br>

  

Example option 3:

|25|..|28|22|..|11|<br>

  

Example option 4:

|25|..|..|28|..|22|11|<br>

  

  

  

  

'simple phase nets' conjecture 2:

Every layer/row can be described by one of 7 states. One state for {1, 1, 1, 1, 0, 0, 0}

and 2 states for each of the other configurations.

There's 2 states for the rest because either the left 2 connected square 'islands' are 'grounded' to the bottom cell without the need of the above layer's help,

or the right 2 are 'grounded' to the bottom cell without the need of the above layer's help. (See: 'Explanation of 'Grounded cells'', for an idea of what is meant by 'grounded')

Example that uses all 7 layer states:

````
|..|..|..|..|##|..|..|
|..|##|##|##|##|..|..|
|..|##|..|##|##|..|##|
|##|##|..|##|..|..|##|
|##|..|..|##|..|##|##|
|..|..|..|##|##|##|##|
|..|##|..|##|##|..|##|
|##|##|..|##|..|..|##|
|##|..|..|##|..|##|##|
|##|##|##|##|..|..|..|
|..|..|##|..|..|..|..|
```
(The bottom half has the left side of the layers grounded from bottom to top without needing higher layers to help ground while
for the top half, it's the opposite)

'simple phase nets' conjecture 3:

  

This formula gets us the number of ways to have a 'simple phase nets' with dimensions 1x1xn:


F(n) = 16 * (1 0 0 0 0 0 0) M^(n-1) (1)

                              (0)

                              (0)

                              (0)

                              (0)

                              (0)

                              (0)

Where the matrix M =

7 2 1 2 2 1 2<br>

1 1 0 1 0 1 0<br>

2 0 1 0 1 0 1<br>

2 1 0 1 0 1 0<br>

2 0 1 0 1 0 1<br>

2 1 0 1 0 1 0<br>

1 0 1 0 1 0 1<br>

  <br>

or more simply:

M=

7 2 2 1 1 2 2<br>

1 1 1 1 0 0 0<br>

2 1 1 1 0 0 0<br>

2 1 1 1 0 0 0<br>

2 0 0 0 1 1 1<br>

2 0 0 0 1 1 1<br>

1 0 0 0 1 1 1<br>

(I got this matrix from permuting the order of the states of the 1st matrix.)

### Notes about this equation
* This only counts the number of fixed solutions. (i.e: this ignores the 2D symmetries)
	* Because there are only up to 8 possible symmetries for a 2D square lattice, F(n) is within a factor of 8 of the number of 'simple phase nets' if you don't ignore the symmetries.
* The reason the matrix is 7x7 is because there's only 7 different possible states an in-between layer could be in and this matrix describes how many ways we could layer state 'i' onto state 'j'.
* I googled for the eigenvectors and eigenvalues and found that the biggest eigenvalue is 9.4956...
	* See below for more details

#### The Matrix Eigenvalue
https://matrixcalc.org/ gives this:
9.4956226893 ...
I also took screenshots of the exact form of the solution from wolframalpha (See the pics folder).
I believe that it's the solution to a cubic polynomial, but I'm not 100% sure.

The eigenvalue found means that for every layer you add to the 1x1xN cuboid, you get around 9.5 times more possible configurations.


I'm currently hoping to find something similar for all 1x1xN nets. I'll look into this later.
