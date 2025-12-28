import requests
from config import API_BASE_URL


class AvisClient:
    """
    Responsable de l'envoi des avis au serveur API.
    """

    def __init__(self, base_url: str | None = None, timeout: float = 5.0):
        #URL de base de l'API (ex: http://172.17.0.1:7000/api)
        self.base_url = (base_url or API_BASE_URL).rstrip("/")
        # Délai d'attente max pour les requêtes
        self.timeout = timeout

    def envoyer_avis(self, avis_json: dict) -> tuple[bool, str]:
        """
        Envoie un avis au serveur API.

        :param avis_json: Dictionnaire contenant les données de l'avis.
        :return: (success, message)
        """
        url = f"{self.base_url}/avis"

        try:
            response = requests.post(url, json=avis_json, timeout=self.timeout)

            if 200 <= response.status_code < 300:
                return True, "Ton avis a été envoyé avec succès."

            elif response.status_code == 400:
                try:
                    data = response.json()
                    message = (
                        data.get("message")
                        or data.get("error")
                        or "Les données envoyées sont invalides."
                    )
                except Exception:
                    message = "Le serveur a refusé les données envoyées."

                return False, f"Erreur de validation : {message}"

            # Autres erreurs HTTP (500, 404, etc.)
            return False, (
                f"Erreur serveur : {response.status_code}. "
                "Réessaie plus tard."
            )

        except requests.exceptions.ConnectionError:
            return False, "Impossible de contacter l'API. Vérifie ta connexion Internet."

        except requests.exceptions.Timeout:
            return False, "L'API a mis trop de temps à répondre. Réessaie plus tard."

        except Exception as e:
            print(f"[AvisClient] Erreur inattendue lors de l'envoi de l'avis : {e}")
            return False, "Une erreur inattendue est survenue lors de l'envoi de ton avis."