import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { 
  LayoutGrid, 
  Package, 
  ShoppingCart, 
  FileText, 
  Menu, 
  X, 
  LogOut,
  Sparkles,
  TrendingUp,
  Users,
  DollarSign
} from "lucide-react";
import "./DashboardAdmin.css";

const DashboardAdmin = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const navigate = useNavigate();

  const menuItems = [
    { 
      icon: LayoutGrid, 
      label: "Catégories", 
      path: "/admin/categories",
      gradient: "gradient-blue"
    },
    { 
      icon: Package, 
      label: "Produits", 
      path: "/admin/produits",
      gradient: "gradient-green"
    },
    { 
      icon: ShoppingCart, 
      label: "Commandes", 
      path: "/admin/commandes",
      gradient: "gradient-yellow"
    },
    { 
      icon: FileText, 
      label: "Factures", 
      path: "/admin/factures",
      gradient: "gradient-purple"
    }
  ];

  const stats = [
    { 
      label: "Revenus", 
      value: "24,500€", 
      icon: DollarSign, 
      colorClass: "stat-green",
      iconBg: "bg-success-light"
    },
    { 
      label: "Commandes", 
      value: "156", 
      icon: ShoppingCart, 
      colorClass: "stat-blue",
      iconBg: "bg-primary-light"
    },
    { 
      label: "Produits", 
      value: "89", 
      icon: Package, 
      colorClass: "stat-purple",
      iconBg: "bg-purple-light"
    },
    { 
      label: "Clients", 
      value: "1,234", 
      icon: Users, 
      colorClass: "stat-orange",
      iconBg: "bg-warning-light"
    }
  ];

  return (
    <div className="dashboard-admin">
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
              <div className="logo-box gradient-admin">
                <Sparkles size={24} />
              </div>
              <span className="navbar-brand mb-0 h3 fw-bold gradient-text-admin">
                YallaShop Admin
              </span>
            </div>
          </div>
          
          <div className="d-flex align-items-center gap-3">
            <div className="d-none d-sm-flex align-items-center gap-2 px-3 py-2 badge-admin">
              <TrendingUp size={16} />
              <span className="small fw-medium">Admin Dashboard</span>
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
          </div>
        </aside>

        {/* Main Content */}
        <main className="main-content flex-grow-1">
          <div className="container-fluid">
            {/* Header */}
            <div className="mb-4 mb-lg-5">
              <h1 className="display-5 fw-bold text-dark mb-2">
                Bienvenue, Admin 
              </h1>
              <p className="text-muted lead">
                Voici un aperçu de votre tableau de bord
              </p>
            </div>

            {/* Stats Grid */}
            <div className="row g-3 g-lg-4 mb-4 mb-lg-5">
              {stats.map((stat, index) => (
                <div key={index} className="col-12 col-sm-6 col-lg-3">
                  <div className="stat-card">
                    <div className="d-flex align-items-center justify-content-between mb-3">
                      <div className={`stat-icon ${stat.iconBg}`}>
                        <stat.icon size={24} className={stat.colorClass} />
                      </div>
                    </div>
                    <p className="text-muted small mb-1">{stat.label}</p>
                    <h2 className="h3 fw-bold mb-0">{stat.value}</h2>
                  </div>
                </div>
              ))}
            </div>

            {/* Quick Actions */}
            <div className="action-card">
              <h2 className="h4 fw-bold mb-4">Actions rapides</h2>
              <div className="row g-3 g-lg-4">
                {menuItems.map((item, index) => (
                  <div key={index} className="col-12 col-sm-6 col-lg-3">
                    <button
                      onClick={() => navigate(item.path)}
                      className="quick-action-btn"
                    >
                      <div className={`action-icon ${item.gradient}`}>
                        <item.icon size={28} />
                      </div>
                      <h5 className="fw-semibold mb-1">Gérer {item.label}</h5>
                      <p className="small text-muted mb-0">Accéder à la gestion</p>
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

export default DashboardAdmin;