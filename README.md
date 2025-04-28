<h1>API Endpoint Reference</h1>

<h2>ActController (<code>/api/acts</code>)</h2>
<ul>
  <li><b>GET /</b> – Retrieves all active acts.</li>
  <li><b>GET /all</b> – Retrieves all acts, including inactive ones.</li>
  <li><b>GET /{id}</b> – Retrieves an active act by ID.</li>
  <li><b>POST /create</b> – Creates a new act.</li>
  <li><b>PUT /{id}/edit</b> – Updates an existing act.</li>
  <li><b>DELETE /{id}</b> – Deactivates (soft-deletes) an act.</li>
  <li><b>POST /restore/{id}</b> – Restores a previously deactivated act.</li>
</ul>

<h2>CategoryController (<code>/api/categories</code>)</h2>
<ul>
  <li><b>GET /</b> – Retrieves all active categories.</li>
  <li><b>GET /all</b> – Retrieves all categories, including inactive ones.</li>
  <li><b>GET /{id}</b> – Retrieves an active category by ID.</li>
  <li><b>POST /create</b> – Creates a new category.</li>
  <li><b>PUT /{id}/edit</b> – Updates an existing category.</li>
  <li><b>DELETE /{id}</b> – Deactivates (soft-deletes) a category.</li>
  <li><b>POST /restore/{id}</b> – Restores a previously deactivated category.</li>
</ul>

<h2>EventController (<code>/api/events</code>)</h2>
<ul>
  <li><b>GET /</b> – Retrieves all events.</li>
  <li><b>GET /{id}</b> – Retrieves an event by ID.</li>
  <li><b>GET /venues</b> – Retrieves all venues.</li>
  <li><b>GET /acts</b> – Retrieves all acts.</li>
  <li><b>POST /</b> – Creates a new event.</li>
  <li><b>PUT /{id}</b> – Updates an event.</li>
  <li><b>DELETE /{id}</b> – Deactivates (soft-deletes) an event.</li>
  <li><b>POST /restore/{id}</b> – Restores a previously deactivated event.</li>
</ul>

<h2>NotificationController (<code>/api/notifications</code>)</h2>
<ul>
  <li><b>GET /</b> – Retrieves all notifications.</li>
  <li><b>GET /all</b> – Retrieves all notifications, including inactive ones.</li>
  <li><b>GET /{id}</b> – Retrieves a notification by ID.</li>
  <li><b>GET /user/{userId}</b> – Retrieves all notifications for a specific user.</li>
  <li><b>GET /user/{userId}/unread</b> – Retrieves unread notifications for a specific user.</li>
  <li><b>POST /</b> – Creates a new notification.</li>
  <li><b>PUT /{id}</b> – Updates a notification.</li>
  <li><b>DELETE /{id}</b> – Deactivates (soft-deletes) a notification.</li>
  <li><b>POST /restore/{id}</b> – Restores a previously deactivated notification.</li>
</ul>

<h2>TagController (<code>/api/tags</code>)</h2>
<ul>
  <li><b>GET /</b> – Retrieves all active tags.</li>
  <li><b>GET /all</b> – Retrieves all tags, including inactive ones.</li>
  <li><b>GET /{id}</b> – Retrieves an active tag by ID.</li>
  <li><b>GET /by-category/{categoryId}</b> – Retrieves tags by category.</li>
  <li><b>POST /</b> – Creates a new tag.</li>
  <li><b>PUT /{id}</b> – Updates a tag.</li>
  <li><b>DELETE /{id}</b> – Deactivates (soft-deletes) a tag.</li>
  <li><b>POST /restore/{id}</b> – Restores a previously deactivated tag.</li>
</ul>

<h2>TicketCategoryController (<code>/api/ticket-categories</code>)</h2>
<ul>
  <li><b>GET /</b> – Retrieves all active ticket categories.</li>
  <li><b>GET /all</b> – Retrieves all ticket categories, including inactive ones.</li>
  <li><b>GET /{id}</b> – Retrieves an active ticket category by ID.</li>
  <li><b>GET /status/{status}</b> – Retrieves ticket categories by status.</li>
  <li><b>GET /search?name=</b> – Retrieves ticket categories by name.</li>
  <li><b>GET /available</b> – Retrieves ticket categories with available tickets.</li>
  <li><b>POST /</b> – Creates a new ticket category.</li>
  <li><b>PUT /{id}</b> – Updates a ticket category.</li>
  <li><b>DELETE /{id}</b> – Deactivates (soft-deletes) a ticket category.</li>
  <li><b>POST /restore/{id}</b> – Restores a previously deactivated ticket category.</li>
</ul>

<h2>TicketController (<code>/api/tickets</code>)</h2>
<ul>
  <li><b>GET /</b> – Retrieves all active tickets.</li>
  <li><b>GET /all</b> – Retrieves all tickets, including inactive ones.</li>
  <li><b>GET /{id}</b> – Retrieves an active ticket by ID.</li>
  <li><b>GET /user/{userId}</b> – Retrieves tickets by user ID.</li>
  <li><b>GET /event/{eventId}</b> – Retrieves tickets by event ID.</li>
  <li><b>GET /status/{status}</b> – Retrieves tickets by status.</li>
  <li><b>POST /</b> – Creates a new ticket.</li>
  <li><b>PUT /{id}</b> – Updates an existing ticket.</li>
  <li><b>DELETE /{id}</b> – Deactivates (soft-deletes) a ticket.</li>
  <li><b>POST /restore/{id}</b> – Restores a previously deactivated ticket.</li>
</ul>

<h2>UserController (<code>/api/users</code>)</h2>
<ul>
  <li><b>POST /register</b> – Registers a new user.</li>
  <li><b>POST /login</b> – Authenticates a user using email and password.</li>
  <li><b>GET /</b> – Retrieves all active users.</li>
  <li><b>GET /all</b> – Retrieves all users, including inactive ones.</li>
  <li><b>GET /{id}</b> – Retrieves a user by ID.</li>
  <li><b>PUT /{id}</b> – Updates a user’s information.</li>
  <li><b>DELETE /{id}</b> – Deactivates (soft-deletes) a user.</li>
  <li><b>POST /restore/{id}</b> – Restores a previously deactivated user.</li>
</ul>

<h2>VenueController (<code>/api/venues</code>)</h2>
<ul>
  <li><b>GET /</b> – Retrieves all active venues.</li>
  <li><b>GET /all</b> – Retrieves all venues, including inactive ones.</li>
  <li><b>GET /{id}</b> – Retrieves an active venue by ID.</li>
  <li><b>POST /create</b> – Creates a new venue (or returns existing if already present).</li>
  <li><b>PUT /{id}</b> – Updates a venue.</li>
  <li><b>DELETE /{id}</b> – Deactivates (soft-deletes) a venue.</li>
  <li><b>POST /restore/{id}</b> – Restores a previously deactivated venue.</li>
</ul>
