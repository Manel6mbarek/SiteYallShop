import React, { useState } from "react";
import { useAuth } from "../context/AuthContext";
import {
  Mail,
  Lock,
  User,
  Phone,
  MapPin,
  ArrowRight,
  Sparkles,
  Shield,
  Zap,
} from "lucide-react";
import "./Login.css";

const Login = () => {
  const { login, register } = useAuth();
  const [isRegister, setIsRegister] = useState(false);
  const [formData, setFormData] = useState({
    email: "",
    motDePasse: "",
    nom: "",
    prenom: "",
    telephone: "",
    adresse: "",
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (isRegister) {
      await register(formData);
      setIsRegister(false);
    } else {
      await login(formData.email, formData.motDePasse);
    }
  };

  return (
    <div className="login-container">
      <div className="background-effects">
        <div className="blob blob-1"></div>
        <div className="blob blob-2"></div>
      </div>

      <div className="container">
        <div className="row g-0 login-card">
          {/* Partie gauche - Info */}
          <div className="col-lg-6 d-none d-lg-flex left-panel">
            <div className="p-5">
              <div className="mb-5">
                <div className="d-flex align-items-center gap-3 mb-4">
                  
                  <h1 className="h2 mb-0 fw-bold">YallaShop</h1>
                </div>
                <h2 className="display-4 fw-bold mb-4">
                  Bienvenue dans<br />votre espace
                </h2>
                <p className="lead">
                  Rejoignez des milliers d'utilisateurs satisfaits qui utilisent
                  YallaShop chaque jour.
                </p>
              </div>

              <div className="features-list">
               

                <div className="feature-item d-flex gap-3">
                  <div className="feature-icon">
                    <Zap size={24} />
                  </div>
                  <div>
                    <h5 className="fw-semibold mb-1">Connexion rapide</h5>
                    <p className="mb-0 text-white-50">
                      Connectez-vous en quelques secondes.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Partie droite - Formulaire */}
          <div className="col-lg-6">
            <div className="form-panel p-4 p-md-5">
              <div className="form-container">
                <div className="text-center mb-4">
                  <h2 className="h3 fw-bold text-white mb-2">
                    {isRegister ? "Créer un compte" : "Bienvenue "}
                  </h2>
                  <p className="text-white-50">
                    {isRegister
                      ? "Remplissez les informations ci-dessous"
                      : "Connectez-vous pour continuer"}
                  </p>
                </div>

                <form onSubmit={handleSubmit}>
                  {isRegister && (
                    <>
                      <div className="row g-3 mb-3">
                        <div className="col-sm-6">
                          <div className="input-group-custom">
                            <User className="input-icon" size={20} />
                            <input
                              name="nom"
                              type="text"
                              placeholder="Nom"
                              onChange={handleChange}
                              required
                              className="form-control form-control-custom"
                            />
                          </div>
                        </div>

                        <div className="col-sm-6">
                          <div className="input-group-custom">
                            <User className="input-icon" size={20} />
                            <input
                              name="prenom"
                              type="text"
                              placeholder="Prénom"
                              onChange={handleChange}
                              required
                              className="form-control form-control-custom"
                            />
                          </div>
                        </div>
                      </div>

                      <div className="mb-3">
                        <div className="input-group-custom">
                          <Phone className="input-icon" size={20} />
                          <input
                            name="telephone"
                            type="tel"
                            placeholder="Téléphone"
                            onChange={handleChange}
                            className="form-control form-control-custom"
                          />
                        </div>
                      </div>

                      <div className="mb-3">
                        <div className="input-group-custom">
                          <MapPin className="input-icon" size={20} />
                          <input
                            name="adresse"
                            type="text"
                            placeholder="Adresse"
                            onChange={handleChange}
                            className="form-control form-control-custom"
                          />
                        </div>
                      </div>
                    </>
                  )}

                  <div className="mb-3">
                    <div className="input-group-custom">
                      <Mail className="input-icon" size={20} />
                      <input
                        name="email"
                        type="email"
                        placeholder="Email"
                        onChange={handleChange}
                        required
                        className="form-control form-control-custom"
                      />
                    </div>
                  </div>

                  <div className="mb-3">
                    <div className="input-group-custom">
                      <Lock className="input-icon" size={20} />
                      <input
                        name="motDePasse"
                        type="password"
                        placeholder="Mot de passe"
                        onChange={handleChange}
                        required
                        className="form-control form-control-custom"
                      />
                    </div>
                  </div>

                  {!isRegister && (
                    <div className="d-flex justify-content-between align-items-center mb-4">
                      <div className="form-check">
                        <input
                          type="checkbox"
                          className="form-check-input"
                          id="remember"
                        />
                        <label className="form-check-label text-white-50" htmlFor="remember">
                          Se souvenir de moi
                        </label>
                      </div>
                      <button type="button" className="btn btn-link text-decoration-none p-0 forgot-password">
                        Mot de passe oublié ?
                      </button>
                    </div>
                  )}

                  <button type="submit" className="btn btn-gradient w-100 py-3 fw-semibold d-flex align-items-center justify-content-center gap-2">
                    {isRegister ? "Créer mon compte" : "Se connecter"}
                    <ArrowRight size={20} />
                  </button>
                </form>

                <div className="text-center mt-4">
                  {isRegister ? (
                    <p className="text-white-50 mb-0">
                      Vous avez déjà un compte ?{" "}
                      <button
                        onClick={() => setIsRegister(false)}
                        className="btn btn-link text-decoration-none p-0 switch-mode"
                      >
                        Se connecter
                      </button>
                    </p>
                  ) : (
                    <p className="text-white-50 mb-0">
                      Pas encore de compte ?{" "}
                      <button
                        onClick={() => setIsRegister(true)}
                        className="btn btn-link text-decoration-none p-0 switch-mode"
                      >
                        Créer un compte
                      </button>
                    </p>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;