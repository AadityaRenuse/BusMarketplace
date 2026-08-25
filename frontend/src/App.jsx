import { useState } from "react";
import "./App.css";
import PostTrip from "./pages/PostTrip";
import FindBus from "./pages/FindBus";
import MyTrips from "./pages/MyTrips";
import ViewBids from "./pages/ViewBids";

function App() {
  const [page, setPage] = useState("home");
  const [selectedTripId, setSelectedTripId] = useState(null);


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
          onBack={() => setPage("my-trips")}
        />
      </div>
    );
  }
  

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
          onViewBids={(tripId) => {
            setSelectedTripId(tripId);
            setPage("view-bids");
          }}
        />
    </div>
  );
}

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

  return (
    <div className="app">
      <nav className="navbar">
        <div className="logo">
          Bus<span>Market</span>
        </div>

        <div className="nav-links">
          <a href="#home">Home</a>

          <button
            className="nav-link-button"
            onClick={() => setPage("my-trips")}
        >
          My Trips
        </button>

          <a href="#bookings">Bookings</a>

          <a href="#agencies">Agencies</a>
        </div>

        <button className="login-btn">
          Login
        </button>
      </nav>

      <main>
        <section className="hero" id="home">
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
              Post your travel requirements and receive competitive
              bids from verified bus operators.
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
            <div className="card-icon">🚌</div>

            <h3>Reverse Bidding</h3>

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
            <p className="tagline">HOW IT WORKS</p>

            <h2>
              Rent a bus in three simple steps
            </h2>
          </div>

          <div className="feature-grid">
            <div className="feature-card">
              <div className="feature-number">01</div>

              <h3>Post your trip</h3>

              <p>
                Tell us your pickup location, destination, dates,
                passengers and requirements.
              </p>
            </div>

            <div className="feature-card">
              <div className="feature-number">02</div>

              <h3>Receive bids</h3>

              <p>
                Verified bus operators compete by submitting their
                best prices for your trip.
              </p>
            </div>

            <div className="feature-card">
              <div className="feature-number">03</div>

              <h3>Choose & book</h3>

              <p>
                Compare the available bids and select the operator
                that works best for you.
              </p>
            </div>
          </div>
        </section>

        <section className="cta">
          <div>
            <p className="tagline">READY TO TRAVEL?</p>

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