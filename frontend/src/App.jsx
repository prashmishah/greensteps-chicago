import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import AppLayout from "./layouts/AppLayout.jsx";
import PublicLayout from "./layouts/PublicLayout.jsx";

import Dashboard from "./pages/Dashboard.jsx";
import ActivitySelection from "./pages/ActivitySelection.jsx";
import ActivityForm from "./pages/ActivityForm.jsx";
import History from "./pages/History.jsx";
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import NotFound from "./pages/NotFound.jsx";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* PUBLIC ROUTES */}
        <Route element={<PublicLayout />}>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
        </Route>

        {/* APP ROUTES */}
        <Route element={<AppLayout />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/add-activity" element={<ActivitySelection />} />
          <Route path="/activity/:type" element={<ActivityForm />} />
          <Route path="/history" element={<History />} />
        </Route>

        {/* FALLBACK */} 
        <Route path="/home" element={<Navigate to="/" replace />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  );
}