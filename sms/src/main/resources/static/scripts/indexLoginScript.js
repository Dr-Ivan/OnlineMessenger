const host_ip = "192.168.0.105:8080";

function hashPassword(p){ return btoa(p); }

async function login(){
    const nameEl = document.getElementById("name");
    const passwordEl = document.getElementById("password");

    const n = nameEl.value;
    const p = passwordEl.value;
    if(!n || !p) return alert("Введите все поля");

    const res = await fetch(
        `http://${host_ip}/users/login`,
        {
        method:"POST",
        headers:{ "Content-Type":"application/json" },
        body:JSON.stringify({
            name: n,
            passwordHash: hashPassword(p)
        })
        }
    );

    if(!res.ok){
        console.error(await res.json());
        return alert("Неверные данные");
    }

    localStorage.setItem("userName", n);
    localStorage.setItem("userPass", hashPassword(p));
    location.href="chat.html";
}
