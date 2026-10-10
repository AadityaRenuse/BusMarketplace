
import { useState } from "react";
import "./App.css";

import PostTrip from "./pages/PostTrip";
import FindBus from "./pages/FindBus";
import MyTrips from "./pages/MyTrips";
import ViewBids from "./pages/ViewBids";
import MyBookings from "./pages/MyBookings";
import AgencyDashboard from "./pages/AgencyDashboard";
import MyBuses from "./pages/MyBuses";
import Login from "./pages/Login";

function App() {
  const [page, setPage] = useState("home");
  const [selectedTripId, setSelectedTripId] = useState(null);
  const [selectedTripStatus, setSelectedTripStatus] = useState(null);

  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem("user");

    return savedUser
      ? JSON.parse(savedUser)
      : null;
  });

  // LOGIN PAGE
  if (page === "login") {
    return (
      <Login
        onLogin={(loggedInUser) => {
          setUser(loggedInUser);
          setPage("home");
        }}
      />
    );
  }

  // VIEW BIDS
  if (page === "view-bids") {
    return (
      <div>
        <nav className="navbar">
          <div className="logo">
            Bus<span>Market</span>
          </div>

          <button
            className="login-btn"
            onClick={() => setPage("my-trips")}
          >
            ← Back to My Trips
          </button>
        </nav>

        <ViewBids
          tripRequestId={selectedTripId}
          tripStatus={selectedTripStatus}
          onBack={() => setPage("my-trips")}
        />
      </div>
    );
  }

  // FIND BUS
  if (page === "find-bus") {
    return (
      <div>
        <nav className="navbar">
          <div className="logo">
            Bus<span>Market</span>
          </div>

          <button
            className="login-btn"
            onClick={() => setPage("home")}
          >
            ← Back Home
          </button>
        </nav>

        <FindBus />
      </div>
    );
  }

  // MY TRIPS
  if (page === "my-trips") {
    return (
      <div>
        <nav className="navbar">
          <div className="logo">
            Bus<span>Market</span>
          </div>

          <button
            className="login-btn"
            onClick={() => setPage("home")}
          >
            ← Back Home
          </button>
        </nav>

        <MyTrips
          onViewBids={(tripId, status) => {
            setSelectedTripId(tripId);
            setSelectedTripStatus(status);
            setPage("view-bids");
          }}
        />
      </div>
    );
  }

  // MY BOOKINGS
  if (page === "my-bookings") {
    return (
      <div>
        <nav className="navbar">
          <div className="logo">
            Bus<span>Market</span>
          </div>

          <button
            className="login-btn"
            onClick={() => setPage("home")}
          >
            ← Back Home
          </button>
        </nav>

        <MyBookings />
      </div>
    );
  }

  // POST TRIP
  if (page === "post-trip") {
    return (
      <div>
        <nav className="navbar">
          <div className="logo">
            Bus<span>Market</span>
          </div>

          <button
            className="login-btn"
            onClick={() => setPage("home")}
          >
            ← Back Home
          </button>
        </nav>

        <PostTrip />
      </div>
    );
  }

  // AGENCY DASHBOARD
  if (page === "agency-dashboard") {
    return (
      <div>
        <nav className="navbar">
          <div className="logo">
            Bus<span>Market</span>
          </div>

          <button
            className="login-btn"
            onClick={() => setPage("home")}
          >
            ← Back Home
          </button>
        </nav>

        <AgencyDashboard
          onMyBuses={() => setPage("my-buses")}
        />
      </div>
    );
  }

  // MY BUSES
  if (page === "my-buses") {
    return (
      <div>
        <nav className="navbar">
          <div className="logo">
            Bus<span>Market</span>
          </div>

          <button
            className="login-btn"
            onClick={() => setPage("agency-dashboard")}
          >
            ← Back to Agency Dashboard
          </button>
        </nav>

        <MyBuses />
      </div>
    );
  }

  // HOME PAGE
  return (
    <div className="app">

      <nav className="navbar">

        <div className="logo">
          Bus<span>Market</span>
        </div>

        <div className="nav-links">

          <a href="#home">
            Home
          </a>

          <button
            className="nav-link-button"
            onClick={() => setPage("my-trips")}
          >
            My Trips
          </button>

          <button
            className="nav-link-button"
            onClick={() => setPage("my-bookings")}
          >
            Bookings
          </button>

          <button
            className="nav-link-button"
            onClick={() => setPage("agency-dashboard")}
          >
            Agencies
          </button>

        </div>

        {user ? (
          <button
            className="login-btn"
            onClick={() => {
              localStorage.removeItem("token");
              localStorage.removeItem("user");
              setUser(null);
              setPage("home");
            }}
          >
            Logout
          </button>
        ) : (
          <button
            className="login-btn"
            onClick={() => setPage("login")}
          >
            Login
          </button>
        )}

      </nav>

      <main>

        <section
          className="hero"
          id="home"
        >

          <div className="hero-content">

            <p className="tagline">
              BUS & MINIBUS RENTAL MARKETPLACE
            </p>

            <h1>
              Find the right bus.
              <br />
              <span>At the right price.</span>
            </h1>

            <p className="hero-text">
              Post your travel requirements and receive
              competitive bids from verified bus operators.
            </p>

            <div className="hero-buttons">

              <button
                className="primary-btn"
                onClick={() => setPage("find-bus")}
              >
                Find a Bus
              </button>

              <button
                className="secondary-btn"
                onClick={() => setPage("post-trip")}
              >
                Post a Trip
              </button>

            </div>

          </div>

          <div className="hero-card">

            <div className="card-icon">
              🚌
            </div>

            <h3>
              Reverse Bidding
            </h3>

            <p>
              Let verified travel agencies compete to offer you
              the best price.
            </p>

            <div className="card-stats">

              <div>
                <strong>100+</strong>
                <small>Operators</small>
              </div>

              <div>
                <strong>24/7</strong>
                <small>Marketplace</small>
              </div>

              <div>
                <strong>₹</strong>
                <small>Best Price</small>
              </div>

            </div>

          </div>

        </section>

        <section className="features">

          <div className="section-heading">

            <p className="tagline">
              HOW IT WORKS
            </p>

            <h2>
              Rent a bus in three simple steps
            </h2>

          </div>

          <div className="feature-grid">

            <div className="feature-card">

              <div className="feature-number">
                01
              </div>

              <h3>
                Post your trip
              </h3>

              <p>
                Tell us your pickup location, destination,
                dates, passengers and requirements.
              </p>

            </div>

            <div className="feature-card">

              <div className="feature-number">
                02
              </div>

              <h3>
                Receive bids
              </h3>

              <p>
                Verified bus operators compete by submitting
                their best prices for your trip.
              </p>

            </div>

            <div className="feature-card">

              <div className="feature-number">
                03
              </div>

              <h3>
                Choose & book
              </h3>

              <p>
                Compare the available bids and select the
                operator that works best for you.
              </p>

            </div>

          </div>

        </section>

        <section className="cta">

          <div>

            <p className="tagline">
              READY TO TRAVEL?
            </p>

            <h2>
              Get the best bus for your journey.
            </h2>

          </div>

          <button
            className="primary-btn"
            onClick={() => setPage("post-trip")}
          >
            Post a Trip
          </button>

        </section>

      </main>

      <footer>

        <div className="logo">
          Bus<span>Market</span>
        </div>

        <p>
          Bus & Minibus Rental Marketplace
        </p>

      </footer>

    </div>
  );
}

export default App;