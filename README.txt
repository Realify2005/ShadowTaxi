Assumptions:

#619 in EDSTEM, I have already implemented alternative logic in which the passenger priority is still dynamic even if
it is already in the taxi and being dropped off. It only stays after it is dropped off to the sideway, trip is done,
and earnings calculated etc. (This is the alternative logic that tutor Charlie Ding mentioned in a reply to few threads
so I have implemented that instead).

#698 in EDSTEM, demo shows that if car collides with taxi that's invincible it does not break, when it reality according
to the specs the car should break since taxi still deals 100 damage regardless.

Since in the demo passenger moves slower than driver when trying to be follow driver, whenever driver enters the taxi,
the passenger will be "teleported" to the taxi. This is so that the passenger will become updated with taxi movement again
and the logic of trip start and end and dropping off passengers etc won't be compromised. This is especially for the case
where the driver is entering the taxi from the bottom (driver move upwards really fast, passenger cannot keep up). This edge
case is not shown in the demo.

#636 in EDSTEM (private), asked if protected is fine for GAME_PROPS, and tutor Charlie Ding mentioned that it is fine.
Therefore, I have used it for all constant attributes.

#259 in EDSTEM. I am not able to make constants that read their values from properties file static AND final at the same
time (even with static {} it won't let me since the game_props and message_props are inside constructors, and to access
them I would need to make them static too which is just not possible). Therefore, I can only make some constants static,
that is, the ones that are "hardcoded" (does not read from properties file). While the ones that reads from properties
file are non-static as reasons per #259 in EDSTEM.