import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { api } from 'boot/axios';
import type { User } from 'components/models';

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('et_token'));
  const user = ref<User | null>(JSON.parse(localStorage.getItem('et_user') ?? 'null'));

  const isAuthenticated = computed(() => !!token.value);
  const firstName = computed(() => user.value?.nome?.split(' ')[0] ?? '');

  function setAuth(newToken: string, newUser: User) {
    token.value = newToken;
    user.value = newUser;
    localStorage.setItem('et_token', newToken);
    localStorage.setItem('et_user', JSON.stringify(newUser));
    api.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
  }

  function logout() {
    token.value = null;
    user.value = null;
    localStorage.removeItem('et_token');
    localStorage.removeItem('et_user');
    delete api.defaults.headers.common['Authorization'];
  }

  // re-attach token on page refresh
  if (token.value) {
    api.defaults.headers.common['Authorization'] = `Bearer ${token.value}`;
  }

  async function login(email: string, password: string) {
    const { data } = await api.post<{ token: string; user: User }>('/api/auth/login', {
      email,
      password,
    });
    setAuth(data.token, data.user);
  }

  async function loginWithGoogle(code: string) {
    const { data } = await api.post<{ token: string; user: User }>('/api/auth/google', {
      code,
    });
    setAuth(data.token, data.user);
  }

  async function register(nome: string, email: string, password: string) {
    const { data } = await api.post<{ token: string; user: User }>('/api/auth/register', {
      nome,
      email,
      password,
    });
    setAuth(data.token, data.user);
  }

  return { token, user, isAuthenticated, firstName, login, loginWithGoogle, register, logout };
});
