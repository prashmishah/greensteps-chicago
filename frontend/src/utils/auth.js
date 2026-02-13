const STORAGE_KEY = "greensteps_user";

export function setActiveUser(user) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
}

export function getActiveUser() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) || "null");
  } catch {
    return null;
  }
}

export function clearActiveUser() {
  localStorage.removeItem(STORAGE_KEY);
}
