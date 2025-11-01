// src/services/CategorieService.jsx
import axios from "axios";

const API_ADMIN = "http://localhost:8080/api/admin/categories";

// Admin - Catégories
export const getAllCategoriesAdmin = () => {
  return axios.get(API_ADMIN);
};

export const getCategorieById = (id) => {
  return axios.get(`${API_ADMIN}/${id}`);
};

export const createCategorie = (categorie) => {
  return axios.post(API_ADMIN, categorie);
};

export const updateCategorie = (id, categorie) => {
  return axios.put(`${API_ADMIN}/${id}`, categorie);
};

export const deleteCategorie = (id) => {
  return axios.delete(`${API_ADMIN}/${id}`);
};

const API_CLIENT = "http://localhost:8080/api/clients/categories";

export const getAllCategoriesClient = async () => {
  return axios.get(API_CLIENT);
};

export const getCategorieByIdClient = async (id) => {
  return axios.get(`${API_CLIENT}/${id}`);
};
