const host_ip = "192.168.0.105:8080";

const me = localStorage.getItem("userName");
const pass = localStorage.getItem("userPass");
let current = null;

if(!me || !pass) location.href="index.html";

loadContacts();

setInterval(() => {
    if (current !== null) loadMessages();
}, 3000);

async function loadContacts(){
    const res = await fetch(`http://${host_ip}/users/${me}/contacts`);
    if(!res.ok) return console.error(await res.json());
    const c = await res.json();
    contacts.innerHTML="";
    c.forEach(u=>{
        const d=document.createElement("div");
        d.className="contact";
        d.innerText=u;
        d.onclick=()=>openChat(u);
        contacts.appendChild(d);
    });
}

function openChat(u){
    current = u;
    title.innerText = "Чат с " + u;

    // Удаляем активный класс у всех контактов
    document.querySelectorAll('.contact').forEach(el => {
        el.classList.remove('active');
    });
    // Добавляем активный класс к выбранному контакту
    document.querySelectorAll('.contact').forEach(el => {
        if(el.innerText === u) el.classList.add('active');
    });

    loadMessages();
}

async function loadMessages(){
    const res = await fetch(`http://${host_ip}/messages/${me}/chat/${current}`);
    if(!res.ok) return;
    const msgs = await res.json();
    messages.innerHTML="";
    msgs.forEach(m=>{
        const r=document.createElement("div");
        r.className="row "+(m.fromUserName===me?"self":"other");
        r.innerHTML=`<div class="bubble" onclick="edit(${m.id},'${m.content}')">${m.content}</div>`;
        messages.appendChild(r);
    });
}

async function send(){
    if(!current) return;
    const t=text.value.trim();
    if(!t) return;

    const res = await fetch(`http://${host_ip}/messages`,{
        method:"POST",
        headers:{ "Content-Type":"application/json" },
        body:JSON.stringify({
            fromUserName: me,
            fromUserPasswordHash: pass,
            toUserName: current,
            content: t
        })
    });

    if(!res.ok) console.error(await res.json());
    text.value="";
    loadMessages();
}

async function edit(id, old){
    const n = prompt("Редактировать (пусто — удалить):", old);
    if(n===null) return;

    if(n===""){
        await fetch(`http://${host_ip}/messages/${me}/${id}`,{ method:"DELETE" });
    } else {
        await fetch(`http://${host_ip}/messages/edit`,{
            method:"PUT",
            headers:{ "Content-Type":"application/json" },
            body:JSON.stringify({
                id,
                fromUserName: me,
                fromUserPasswordHash: pass,
                toUserName: current,
                content: n
            })
        });
    }
    loadMessages();
}

async function startChat(){
    const u=newChat.value.trim();
    if(!u||u===me) return;
    openChat(u);
    loadContacts();
}

async function deleteAccount(){
    if(!confirm("Удалить аккаунт?")) return;
    const res = await fetch(`http://${host_ip}/users/${me}/${pass}`,{method:"DELETE"});
    if(res.ok){
        localStorage.clear();
        location.href="index.html";
    } else console.error(await res.json());
}
