import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { 
  LayoutGrid, 
  Package, 
  ShoppingBag, 
  FileText, 
  Menu, 
  X, 
  LogOut,
  Sparkles,
  Heart,
  Clock,
  Star
} from "lucide-react";
import "./DashboardClient.css";

const DashboardClient = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const navigate = useNavigate();

  const menuItems = [
    { 
      icon: LayoutGrid, 
      label: "Catégories", 
      path: "/client/categories",
      gradient: "gradient-emerald",
      description: "Explorer les catégories"
    },
    { 
      icon: Package, 
      label: "Produits", 
      path: "/client/produits",
      gradient: "gradient-blue",
      description: "Parcourir le catalogue"
    },
    { 
      icon: ShoppingBag, 
      label: "Mes Commandes", 
      path: "/client/commandes",
      gradient: "gradient-orange",
      description: "Suivre mes achats"
    },
    { 
      icon: FileText, 
      label: "Factures", 
      path: "/client/factures",
      gradient: "gradient-purple",
      description: "Consulter mes factures"
    }
  ];

  const recentActivities = [
    { icon: ShoppingBag, text: "Commande #1234 livrée", time: "Il y a 2h", colorClass: "activity-green" },
    { icon: Heart, text: "3 produits ajoutés aux favoris", time: "Hier", colorClass: "activity-red" },
    { icon: Star, text: "Avis laissé sur un produit", time: "Il y a 3j", colorClass: "activity-yellow" }
  ];

  const quickStats = [
    {
      label: "Commandes",
      value: "12",
      icon: ShoppingBag,
      iconBg: "bg-orange-light",
      iconColor: "text-orange"
    },
    {
      label: "Favoris",
      value: "8",
      icon: Heart,
      iconBg: "bg-red-light",
      iconColor: "text-red"
    },
    {
      label: "En cours",
      value: "3",
      icon: Clock,
      iconBg: "bg-purple-light",
      iconColor: "text-purple"
    }
  ];

  return (
    <div className="dashboard-client">
      {/* Navbar */}
      <nav className="navbar navbar-expand-lg navbar-light bg-white border-bottom sticky-top shadow-sm">
        <div className="container-fluid px-3 px-lg-4">
          <div className="d-flex align-items-center gap-3">
            <button 
              onClick={() => setIsSidebarOpen(!isSidebarOpen)}
              className="btn btn-icon-toggle"
            >
              {isSidebarOpen ? <X size={24} /> : <Menu size={24} />}
            </button>
            
            <div className="d-flex align-items-center gap-3">
              <div className="logo-box gradient-client">
                <Sparkles size={24} />
              </div>
              <span className="navbar-brand mb-0 h3 fw-bold gradient-text-client">
                YallaShop
              </span>
            </div>
          </div>
          
          <div className="d-flex align-items-center gap-3">
            <div className="d-none d-sm-flex align-items-center gap-2 px-3 py-2 badge-client">
              <Heart size={16} />
              <span className="small fw-medium">Espace Client</span>
            </div>
            <button className="btn btn-icon-logout">
              <LogOut size={20} />
            </button>
          </div>
        </div>
      </nav>

      <div className="d-flex">
        {/* Sidebar */}
        <aside className={`sidebar ${isSidebarOpen ? 'sidebar-open' : 'sidebar-closed'}`}>
          <div className="sidebar-content">
            <div className="nav flex-column gap-2">
              {menuItems.map((item, index) => (
                <button
                  key={index}
                  onClick={() => navigate(item.path)}
                  className="btn btn-menu-item"
                >
                  <div className={`menu-icon ${item.gradient}`}>
                    <item.icon size={20} />
                  </div>
                  <span className="menu-label">{item.label}</span>
                </button>
              ))}
            </div>

            {/* Activity Section */}
            <div className="activity-section">
              <h6 className="activity-title">Activité récente</h6>
              <div className="activity-list">
                {recentActivities.map((activity, index) => (
                  <div key={index} className="activity-item">
                    <activity.icon size={16} className={activity.colorClass} />
                    <div className="activity-content">
                      <p className="activity-text">{activity.text}</p>
                      <p className="activity-time">{activity.time}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </aside>

        {/* Main Content */}
        <main className="main-content flex-grow-1">
          <div className="container-fluid">
            {/* Welcome Banner */}
            <div className="welcome-banner mb-4 mb-lg-5">
              <div className="row align-items-center">
                <div className="col-lg-8">
                  <h1 className="display-5 fw-bold text-white mb-2">
                    Bienvenue ! 
                  </h1>
                  <p className="lead text-white-80 mb-0">
                    Découvrez nos dernières offres et produits
                  </p>
                </div>
                <div className="col-lg-4 d-none d-lg-block">
                  <div className="welcome-decoration"></div>
                </div>
              </div>
            </div>

            {/* Quick Stats */}
            <div className="row g-3 g-lg-4 mb-4 mb-lg-5">
              {quickStats.map((stat, index) => (
                <div key={index} className="col-12 col-sm-4">
                  <div className="quick-stat-card">
                    <div className="d-flex align-items-center gap-3">
                      <div className={`stat-icon ${stat.iconBg}`}>
                        <stat.icon size={24} className={stat.iconColor} />
                      </div>
                      <div>
                        <p className="text-muted small mb-0">{stat.label}</p>
                        <h3 className="h4 fw-bold mb-0">{stat.value}</h3>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Main Actions */}
            <div className="action-card">
              <h2 className="h4 fw-bold mb-4">Que souhaitez-vous faire ?</h2>
              <div className="row g-3 g-lg-4">
                {menuItems.map((item, index) => (
                  <div key={index} className="col-12 col-sm-6 col-lg-3">
                    <button
                      onClick={() => navigate(item.path)}
                      className="main-action-btn"
                    >
                      <div className={`action-icon-large ${item.gradient}`}>
                        <item.icon size={32} />
                      </div>
                      <h5 className="fw-semibold mb-2">{item.label}</h5>
                      <p className="small text-muted mb-0">{item.description}</p>
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default DashboardClient;