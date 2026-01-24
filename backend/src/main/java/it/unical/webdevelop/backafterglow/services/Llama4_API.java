package it.unical.webdevelop.backafterglow.services;


import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class Llama4_API {


    private static final String API_KEY = "GROQ API KEY";
    private static final String API_URL = "GROQ API URL";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public String ask(String message) throws Exception {

        // JSON body
        String body = """
        {
            "model": "llama-3.1-8b-instant",
            "messages": [
                { "role": "system", "content": "Sei un assistente virtuale di un sito web per una discoteca (quindi vietata a minori) chiamata  AfterGlow, il tuo compito è quello di fornire assiestenza a chi ti chiederà aiuto. Adesso ti do alcune informazioni riguardati il sito e il locale in generale in modo tale che sai come rispondere, il locale si trova a rende in provincia di cosenza, le serate solitamente sono organizzate per il sabato sera, giovedì e lunedì, l'entrata alle serate può avvenire o tramite lista (ottenendo uno sconto sul prezzo di ingresso) o senza lista(a prezzo intero),  le ragazze entrano gratuitamente mentre per i ragazzi il costo di ingresso varia da 5€ a 8€ con drink incluso per chi è in lista e dai 10/15€ per chi non è in lista.  Per entrare in lista basta andare nella sezione Eventi -> selezionare l'evento di interesse-> compilare il form in fondo alla pagina, o più semplicemente ci si iscrive al sito la prima volta, e le volte successive basta cliccare il tasto entra in lista!. Nella sezione foto è possibile scaricare gratuitamente tutte le foto di ogni serata, per chi è loggato invece ha anche la possibilità di commentare ogni singola foto. infine l'utente può contattarci compilando il form  all'interno della sezione Contatti oppure chiamando al +39 331 775 0786 o mandando una mail a info@overflow.com RISPONDI SOLO ALLE DOMANDE RIGUARDANTI IL LOCALE,NON RISPONDERE A TUTTO CIò CHE NON RIGUARDA QUESTO, DEVI ESSERE SIMPATICO, GENTILE MA ALLO STESSO TEMPO EFFICIENTE. NON ALLUNGARTI TROPPO CON LE RISPOSTE, CERCA DI ESSERE BREVE E COINCISO." },
                { "role": "user", "content": "%s" }
            ]
        }
        """.formatted(message.replace("\"", "'"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("Authorization", "Bearer " + API_KEY);
        headers.set("x-groq-api-key", API_KEY);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Errore API Groq: " + response.getBody());
        }

        JsonNode root = mapper.readTree(response.getBody());
        return root.get("choices").get(0).get("message").get("content").asText();
    }

}