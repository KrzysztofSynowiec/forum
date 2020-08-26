I used Spring boot, Hibernate, and PostgreSQL to solve the challenge below.
My goal was to obtain the most efficient code possible, minimizing the need for communication with the database.
Additionally, I made it possible to edit and delete answers (not only the topic)

The challenge is to design and program a simple backend (HTTP + REST) ​​for a discussion forum like... http://kafeteria.pl/forum
As a not logged in user:
- I can create a new topic for discussion, providing: subject, content of the post, nickname and email address,
- I can answer to the selected topic of discussion by providing: post content, nickname and email address,
- I can edit the post (its content) by providing: new content, secret for a given post - a unique secret related to the post is received each time a new post is added (the mechanism is analogous to editing via a link in the email - only simplified),
- I can delete a post by giving a secret for a given post,

- I can view the list of topics sorted from the most to the least active topics (the activity of a topic is measured by the last time someone responded to it), additionally I can give a limit and an offset to get a simple pagination (the limit should be limited by the maximum value read from the configuration file - to it was not possible to enumerate the entire contents of the database "at once")

- I can view replies on a given topic from the oldest (first) post to the newest post; here pagination is a bit more difficult (a'la Reddit if you associate) - we give the id of the "middle" element, the number of elements before and the number of elements after the middle element (according to the date they were added) - in such a paginated list there is always the middle element and the number of elements before and after him; but attention - the maximum limit read from the configuration file (mentioned earlier) still applies - if MAX_LIMIT <(before + after + 1) we have to truncate the list proportionally - e.g. if MAX_LIMIT = 50, before = 50, after = 100, id = X (before: after = 1: 2) then the list will have 50 elements in total - 17 before elements, middle element (id = X), 32 after elements (rounding can be arbitrarily “polarized”).

Additionally:
- reasonable data validation is required - e.g. by email, the length of the topic, post or nickname
- if something is not described from a business point of view - we allow "reasonable freedom"

It is important:
- code quality
- proper design of REST-like endpoints (or REST-like if you are not a purist) - paths, HTTP methods, appropriate HTTP and JSON codes (also errors returned in JSON)
- sensible (strong) use of types
- welcome (but completely not required) tests - integrated, to a lesser extent unitary

Preferred technology stack (but deviations are allowed - just let me know in advance):
- Java
- Spring
- Hibernate
- PostgreSQL