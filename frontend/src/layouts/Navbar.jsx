function Navbar() {
  return (
    <div className="navbar">
      <div className="navbar-logo">
        🌿 Green Steps
      </div>

      <div className="navbar-actions">
        <button className="nav-btn">History</button>
        <button className="logout-btn">Logout</button>
      </div>
    </div>
  );
}

export default Navbar;