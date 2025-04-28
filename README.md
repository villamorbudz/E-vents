ActController (/api/acts)

GET /
Retrieves all active acts.

GET /all
Retrieves all acts, including inactive ones.

GET /{id}
Retrieves an active act by ID.

POST /create
Creates a new act.

PUT /{id}/edit
Updates an existing act.

DELETE /{id}
Deactivates (soft-deletes) an act.

POST /restore/{id}
Restores a previously deactivated act.

CategoryController (/api/categories)

GET /
Retrieves all active categories.

GET /all
Retrieves all categories, including inactive ones.

GET /{id}
Retrieves an active category by ID.

POST /create
Creates a new category.

PUT /{id}/edit
Updates an existing category.

DELETE /{id}
Deactivates (soft-deletes) a category.

POST /restore/{id}
Restores a previously deactivated category.

EventController (/api/events)

GET /
Retrieves all events.

GET /{id}
Retrieves an event by ID.

GET /venues
Retrieves all venues.

GET /acts
Retrieves all acts.

POST /
Creates a new event.

PUT /{id}
Updates an event.

DELETE /{id}
Deactivates (soft-deletes) an event.

POST /restore/{id}
Restores a previously deactivated event.

NotificationController (/api/notifications)

GET /
Retrieves all notifications.

GET /all
Retrieves all notifications, including inactive ones.

GET /{id}
Retrieves a notification by ID.

GET /user/{userId}
Retrieves all notifications for a specific user.

GET /user/{userId}/unread
Retrieves unread notifications for a specific user.

POST /
Creates a new notification.

PUT /{id}
Updates a notification.

DELETE /{id}
Deactivates (soft-deletes) a notification.

POST /restore/{id}
Restores a previously deactivated notification.

TagController (/api/tags)

GET /
Retrieves all active tags.

GET /all
Retrieves all tags, including inactive ones.

GET /{id}
Retrieves an active tag by ID.

GET /by-category/{categoryId}
Retrieves tags by category.

POST /
Creates a new tag.

PUT /{id}
Updates a tag.

DELETE /{id}
Deactivates (soft-deletes) a tag.

POST /restore/{id}
Restores a previously deactivated tag.

TicketCategoryController (/api/ticket-categories)

GET /
Retrieves all active ticket categories.

GET /all
Retrieves all ticket categories, including inactive ones.

GET /{id}
Retrieves an active ticket category by ID.

GET /status/{status}
Retrieves ticket categories by status.

GET /search?name=
Retrieves ticket categories by name.

GET /available
Retrieves ticket categories with available tickets.

POST /
Creates a new ticket category.

PUT /{id}
Updates a ticket category.

DELETE /{id}
Deactivates (soft-deletes) a ticket category.

POST /restore/{id}
Restores a previously deactivated ticket category.

TicketController (/api/tickets)

GET /
Retrieves all active tickets.

GET /all
Retrieves all tickets, including inactive ones.

GET /{id}
Retrieves an active ticket by ID.

GET /user/{userId}
Retrieves tickets by user ID.

GET /event/{eventId}
Retrieves tickets by event ID.

GET /status/{status}
Retrieves tickets by status.

POST /
Creates a new ticket.

PUT /{id}
Updates an existing ticket.

DELETE /{id}
Deactivates (soft-deletes) a ticket.

POST /restore/{id}
Restores a previously deactivated ticket.

UserController (/api/users)

POST /register
Registers a new user.

POST /login
Authenticates a user using email and password.

GET /
Retrieves all active users.

GET /all
Retrieves all users, including inactive ones.

GET /{id}
Retrieves a user by ID.

PUT /{id}
Updates a user’s information.

DELETE /{id}
Deactivates (soft-deletes) a user.

POST /restore/{id}
Restores a previously deactivated user.

VenueController (/api/venues)

GET /
Retrieves all active venues.

GET /all
Retrieves all venues, including inactive ones.

GET /{id}
Retrieves an active venue by ID.

POST /create
Creates a new venue (or returns existing if already present).

PUT /{id}
Updates a venue.

DELETE /{id}
Deactivates (soft-deletes) a venue.

POST /restore/{id}
Restores a previously deactivated venue.
