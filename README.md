# Assignment 2
## Instructions

1. Open command line in project root
2. Run the following commands:

Commands for problem 1:
- `javac cupcake.java`
- `java cupcake`

Commands for problem 2:
- `javac vase.java`
- `java vase`

3. View the output in the command line

## Problem 1 Explanation 

### Assumptions
1. Guests are picked at random to enter
2. Guests can be picked an arbitrary amount of times
3. The labyrinth starts with cake 

### Algorithm Implemented

1. Assign one guest as leader
2. Let count = 0
3. Select a guest to enter at random
4. If the leader enters:
a. If this is the first time entering, increment count
b. If there is no cake: increment count and request new cake
5. If a non-leader guest enters:
a. If this is the first time entering and there is a cupcake: eat cupcake
6. Optionally: go to 3
7. If count equals the number of guests, all guests have entered
8. Else: Not all guests entered or unable to tell if all guests entered

### Algorithm Reasoning

The idea behind this algorithm is that a leader is tasked to keep track of the times he finds missing cakes. Each missing cake represents the first time a non-leader enters and eats a cake. This is the case because non-leaders only eat a cake if it is their first and never request a new cake. Therefore, if a leader finds a missing cake he knows that a non-leader has entered for the first time and no other guest has interfered with this information.

It is important to note that all guests could have entered but it is unknown to the leader. For example the last guest to enter eats the cake, making it their first time. However, if the party ends before the leader can go back in a see the missing cake. In this cases, the leader does not know that all guests have entered. 

### Implementation

For the implementation, the java `ExecutorService` is used to manage threads. The number of threads(guests) is specified by the user. The implementation strictly follows the algorithm described. Due to the nature of the program, performance is not a huge concern as it is heavily dependent on frequent user input. The program was tested by using sample user input.

## Problem 1 Explanation 

### Assumptions
1. The guests do not need to enter the queue right away
2. The guests in the queue enter the room as soon as they can
3. The guests do not need to leave the room right away
4. The guests will wait and then re-enter the queue until the user terminates the program
5. A guest may only occupy one position in the queue at a time

### Algorithm Implemented
I chose to implement algorithm 3: "The third strategy would allow the quests to line in a queue. Every guest exiting the room was responsible to notify the guest standing in front of the queue that the showroom is available. Guests were allowed to queue multiple times"

### Algorithm Reasoning

The reason that I chose this algorithm is because option 1 and 2 could allow "starvation" for a guest. This means it is possible that a guests who wants to enter may never get the chance. Therefore, I found option 3 to be the best choice.

One down side for option 3 is that guests need to form and manage a queue. For example, if the guests simply form a line, they are unable to move throughout the house while the room is occupied. On the other hand, guests could exchange phone numbers and use a shared communication system. For example, the guests all join a group chat to communicate queue information and who's turn it is to enter the room. This of course would require the guests to setup and maintain this queue information group chat which could be a down side. 

### Implementation

For the implementation, the java `ExecutorService` is used to manage threads. The number of threads(guests) is specified by the user. At start the guests wait idly in the house for some time until they want to enter the queue for the room. This initial waiting is done by putting the guest thread to sleep for some time. When it is the guests turn to enter, they leave the queue and enter the room and signal that they are inside. They then idle in the room for a certain amount of time similar as before. This is once again done by putting the thread asleep. When the thread awakes, the guests exit and signals that the room is open. 

When some action is taken by guests, information about the action and the current state of the program is printed to the user. To prevent any issues from data being modified during the print process, a lock is palaced prevent any guests from taking action. To test the program, sample input was provided and verified that the print statements were correct. Because threads are often put to sleep, performance is not a major concern. 