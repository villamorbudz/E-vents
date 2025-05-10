import { useState, useEffect } from "react";

// Mock notification data based on the Java model
const mockNotifications = [
  {
    notificationId: 1,
    title: "Event Reminder",
    message: "Your event 'TYLER, THE CREATOR: CHROMAKOPIA THE WORLD TOUR' is happening tomorrow at ARANETA COLISEUM. Don't forget your tickets!",
    type: "REMINDER",
    read: false,
    createdAt: "2025-05-09T10:30:00",
    event: {
      id: 1,
      title: "TYLER, THE CREATOR: CHROMAKOPIA THE WORLD TOUR"
    }
  },
  {
    notificationId: 2,
    title: "Event Update",
    message: "The start time for 'MUSIC FESTIVAL' has been changed from 8PM to 7PM. Please plan accordingly.",
    type: "EVENT_UPDATE",
    read: false,
    createdAt: "2025-05-08T14:15:00",
    event: {
      id: 2,
      title: "MUSIC FESTIVAL:"
    }
  },
  {
    notificationId: 3,
    title: "New Ticket Available",
    message: "VIP tickets for 'TAYLOR SWIFT: THE ERAS TOUR' are now available. Get them before they're sold out!",
    type: "SYSTEM",
    read: true,
    createdAt: "2025-05-05T09:00:00",
    event: {
      id: 3,
      title: "TAYLOR SWIFT: THE ERAS TOUR"
    }
  },
  {
    notificationId: 4,
    title: "Event Reminder",
    message: "Your event 'ANIME CONVENTION 2025' is coming up in 5 days. Get ready for an amazing experience!",
    type: "REMINDER",
    read: true,
    createdAt: "2025-05-01T11:45:00",
    event: {
      id: 4,
      title: "ANIME CONVENTION 2025"
    }
  }
];

export default function NotificationsComponent() {
  const [isOpen, setIsOpen] = useState(false);
  const [notifications, setNotifications] = useState(mockNotifications);
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    // Count unread notifications
    const count = notifications.filter(notif => !notif.read).length;
    setUnreadCount(count);
  }, [notifications]);

  const toggleNotifications = () => {
    setIsOpen(!isOpen);
  };

  const markAsRead = (id) => {
    setNotifications(prevNotifications => 
      prevNotifications.map(notif => 
        notif.notificationId === id 
          ? { ...notif, read: true } 
          : notif
      )
    );
  };

  const markAllAsRead = () => {
    setNotifications(prevNotifications => 
      prevNotifications.map(notif => ({ ...notif, read: true }))
    );
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      month: 'short', 
      day: 'numeric',
      hour: '2-digit', 
      minute: '2-digit'
    });
  };

  return (
    <div className="relative">
      <button 
        className="p-2 rounded-full hover:bg-red-700 transition relative"
        onClick={toggleNotifications}
      >
        {/* Bell Icon SVG */}
        <svg 
          width="24" 
          height="24" 
          viewBox="0 0 24 24" 
          fill="none" 
          stroke="currentColor" 
          strokeWidth="2" 
          strokeLinecap="round" 
          strokeLinejoin="round"
        >
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
          <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
        </svg>
        {unreadCount > 0 && (
          <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center">
            {unreadCount}
          </span>
        )}
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 bg-gray-800 rounded-lg shadow-lg overflow-hidden z-50">
          <div className="p-3 bg-gray-700 flex justify-between items-center">
            <h3 className="font-semibold">Notifications</h3>
            {unreadCount > 0 && (
              <button 
                className="text-xs text-red-400 hover:text-red-300"
                onClick={markAllAsRead}
              >
                Mark all as read
              </button>
            )}
          </div>
          
          <div className="max-h-80 overflow-y-auto">
            {notifications.length > 0 ? (
              notifications.map(notification => (
                <div 
                  key={notification.notificationId} 
                  className={`p-3 border-b border-gray-700 hover:bg-gray-700 cursor-pointer transition ${!notification.read ? 'bg-gray-700 bg-opacity-50' : ''}`}
                  onClick={() => markAsRead(notification.notificationId)}
                >
                  <div className="flex justify-between">
                    <h4 className="font-medium text-sm">{notification.title}</h4>
                    <span className="text-xs text-gray-400">{formatDate(notification.createdAt)}</span>
                  </div>
                  <p className="text-sm text-gray-300 mt-1">{notification.message}</p>
                  {!notification.read && (
                    <div className="w-2 h-2 bg-red-500 rounded-full absolute top-3 right-3"></div>
                  )}
                </div>
              ))
            ) : (
              <div className="p-4 text-center text-gray-400">No notifications</div>
            )}
          </div>
          
          <div className="p-2 bg-gray-700 text-center">
            <button className="text-xs text-gray-400 hover:text-white">
              View all notifications
            </button>
          </div>
        </div>
      )}
    </div>
  );
}