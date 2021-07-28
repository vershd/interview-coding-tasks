Live coding session, 45 minutes, 3 sequentially given problems

Basic idea: Code a load balancer

1 - Register instances
Address should be unique, it should not be possible to register the same address two times
Load balancer should accept up to 10 addresses

2 - Random invocation
Develop an algorithm that, when invoking the Load Balancer's get() method multiple times,
should return one backend-instance choosing between the registered ones randomly.

3 - Round-Robin invocation
Develop an algorithm that, when invoking multiple times the Load Balancer on its get() method, should
return one backend-instance choosing between the registered one sequentially (round-robin).


Constraints:
- Do not use external dependencies - only basic JDK
- Setup clean project with test dependencies beforehand (used JUnit5 vanilla)