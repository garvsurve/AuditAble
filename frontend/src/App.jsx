import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Report from './pages/Report';

function App() {
  return (
    <BrowserRouter>
      <div className="app-wrapper">
        <div className="bg-blobs">
          <div className="blob blob-1"></div>
          <div className="blob blob-2"></div>
        </div>
        <main className="container">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/report" element={<Report />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;
