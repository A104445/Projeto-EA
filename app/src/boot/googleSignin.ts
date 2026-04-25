import { defineBoot } from '#q-app/wrappers';
import GoogleSignInPlugin from 'vue3-google-signin';

export default defineBoot(({ app }) => {
  const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
  if (!clientId) return;

  app.use(GoogleSignInPlugin, {
    clientId,
  });
});
