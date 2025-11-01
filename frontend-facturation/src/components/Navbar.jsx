import React from "react";
import { useNavigate } from "react-router-dom";

const Navbar = () => {
  const navigate = useNavigate();

  return (
    <aside className="w-64 bg-white p-6 shadow-md h-screen">
      <h2 className="text-2xl font-bold mb-6">Admin Dashboard</h2>
      <nav className="flex flex-col gap-3">
        <button
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          onClick={() => navigate("/admin/categories")}
        >
          Categories
        </button>
        <button
          className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
          onClick={() => navigate("/admin/produits")}
        >
          Produits
        </button>
        <button
          className="bg-yellow-600 text-white px-4 py-2 rounded hover:bg-yellow-700"
          onClick={() => navigate("/admin/commandes")}
        >
          Commandes
        </button>
        <button
          className="bg-purple-600 text-white px-4 py-2 rounded hover:bg-purple-700"
          onClick={() => navigate("/admin/factures")}
        >
          Factures
        </button>
      </nav>
    </aside>
  );
};

export default Navbar;
