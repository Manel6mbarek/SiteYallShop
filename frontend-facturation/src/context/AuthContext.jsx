// src/context/AuthContext.jsx
import React, { createContext, useContext, useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);

  // Charger user et token depuis localStorage au démarrage
  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    const storedToken = localStorage.getItem("token");
    if (storedUser && storedToken) {
      setUser(JSON.parse(storedUser));
      setToken(storedToken);
    }
  }, []);

  const login = async (email, motDePasse) => {
    try {
      const response = await axios.post("http://localhost:8080/api/auth/login", {
        email,
        motDePasse,
      });
      const userData = response.data;

      localStorage.setItem("token", userData.token);
      localStorage.setItem("user", JSON.stringify(userData));

      setUser(userData);
      setToken(userData.token);

      if (userData.role === "ADMIN") {
        navigate("/admin/dashboard");
      } else {
        navigate("/client/dashboard");
      }
    } catch (error) {
      console.error(error);
      alert(error.response?.data?.message || "Erreur lors de la connexion");
    }
  };

  const register = async (clientData) => {
    try {
      await axios.post("http://localhost:8080/api/auth/register", clientData);
      alert("Inscription réussie ! Vous pouvez maintenant vous connecter.");
    } catch (error) {
      console.error(error);
      alert(error.response?.data || "Erreur lors de l'inscription");
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
    setToken(null);
    navigate("/login");
  };

  return (
    <AuthContext.Provider value={{ user, token, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth doit être utilisé dans un <AuthProvider>");
  return context;
};

export { AuthContext };
