const host_ip = "192.168.0.105:8080";

function hashPassword(p){ return btoa(p); }

async function register(){
    const nameEl = document.getElementById("name");
    const passEl = document.getElementById("password");
    const userName = nameEl.value.trim();
    const pass = passEl.value.trim();

    if(!userName || !pass) return alert("Введите все поля");

    const res = await fetch(`http://${host_ip}/users`,{
        method:"POST",
        headers:{ "Content-Type":"application/json" },
        body:JSON.stringify({
            name: userName,
            passwordHash: hashPassword(pass)
        })
    });

    if(!res.ok){
        console.error(await res.json());
        return alert("Ошибка регистрации. Попробуйте другой никнейм, он должен быть уникальным.");
    }

    alert("Аккаунт создан");
    location.href="index.html";
}
