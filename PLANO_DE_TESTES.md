# Plano de Testes - Consumo do Carro

## ✅ Dispositivo Detectado
**Dispositivo físico conectado:** 45181FDAQ0069Y

---

## 🚀 Passo 1: Instalar o App

Execute no terminal:

```bash
cd "C:\Users\leorz\StudioProjects\ConsumodoCarro"
export JAVA_HOME="C:\Users\leorz\.jdks\openjdk-24.0.2+12-54"
./gradlew installDebug
```

Ou se preferir, abra o projeto no Android Studio e clique em "Run" (▶️).

---

## 📋 Passo 2: Cenários de Teste

### **TESTE 1: Tela Vazia (Primeiro Uso)**

**Objetivo:** Verificar estado inicial do app

**Passos:**
1. Abra o app
2. Você deve ver a tela de histórico VAZIA com:
   - Título: "Consumo do Carro"
   - Mensagem: "Nenhum abastecimento registrado"
   - Mensagem: "Toque em + para adicionar"
   - Botão flutuante (+) no canto inferior direito

**✅ Esperado:** Tela vazia com mensagem amigável

---

### **TESTE 2: Adicionar Primeiro Abastecimento**

**Objetivo:** Cadastrar o primeiro registro

**Passos:**
1. Toque no botão flutuante (+)
2. Você será levado para a tela "Novo Abastecimento"
3. Preencha os dados:
   - **Quilometragem:** 50000
   - **Litros:** 40
   - **Preço por litro:** 5.50
   - **Tipo:** Gasolina
   - **Observações:** Primeiro abastecimento (opcional)
4. Observe que o **Valor total** é calculado automaticamente: **R$ 220,00**
5. Toque em "Salvar"

**✅ Esperado:**
- Mensagem: "Abastecimento registrado!"
- Volta para a tela de histórico
- Mostra 1 card com os dados inseridos
- **NÃO mostra consumo** (pois é o primeiro registro)
- Mensagem: "Primeiro abastecimento - consumo será calculado no próximo"
- Card de Estatísticas mostra: "Total de abastecimentos: 1"
- **NÃO mostra "Consumo médio"** ainda

---

### **TESTE 3: Adicionar Segundo Abastecimento (Cálculo de Consumo)**

**Objetivo:** Verificar cálculo automático de consumo

**Passos:**
1. Toque no botão (+) novamente
2. Preencha os dados:
   - **Quilometragem:** 50500 (rodou 500 km)
   - **Litros:** 35
   - **Preço por litro:** 5.60
   - **Tipo:** Gasolina
3. Valor total calculado: **R$ 196,00**
4. Toque em "Salvar"

**✅ Esperado:**
- Volta para a tela de histórico
- Agora mostra 2 cards
- O card do **segundo abastecimento** (mais recente, no topo) mostra:
  - **Consumo: 12,50 km/l** (500 km ÷ 40 litros = 12,5)
  - Card destacado em cor secundária
- O primeiro abastecimento (embaixo) continua sem consumo
- Card de Estatísticas agora mostra:
  - Total de abastecimentos: **2**
  - **Consumo médio: 12,50 km/l**

---

### **TESTE 4: Adicionar Terceiro Abastecimento**

**Objetivo:** Verificar cálculo com múltiplos registros

**Passos:**
1. Toque no botão (+)
2. Preencha:
   - **Quilometragem:** 51000 (rodou 500 km)
   - **Litros:** 38
   - **Preço por litro:** 5.55
   - **Tipo:** Etanol
3. Valor total: **R$ 210,90**
4. Toque em "Salvar"

**✅ Esperado:**
- 3 cards na lista
- **Terceiro abastecimento** (topo) mostra:
  - Consumo: **14,29 km/l** (500 km ÷ 35 litros ≈ 14,29)
  - Tipo: Etanol
- **Segundo abastecimento** mostra: 12,50 km/l
- **Primeiro abastecimento** sem consumo
- Estatísticas:
  - Total: **3**
  - Consumo médio: **13,39 km/l** (média de 12,50 e 14,29)

---

### **TESTE 5: Validação de Campos**

**Objetivo:** Verificar validação de entrada

**Passos:**
1. Toque no botão (+)
2. Deixe todos os campos em branco
3. Toque em "Salvar"
4. **Deve mostrar:** "Digite uma quilometragem válida"

5. Preencha apenas a quilometragem: 52000
6. Toque em "Salvar"
7. **Deve mostrar:** "Digite uma quantidade de litros válida"

8. Preencha quilometragem (52000) e litros (40)
9. Toque em "Salvar"
10. **Deve mostrar:** "Digite um preço válido"

11. Preencha todos os campos corretamente
12. Toque em "Cancelar"
13. **Deve voltar** para a tela de histórico **SEM salvar**

**✅ Esperado:** Validações funcionando, mensagens claras

---

### **TESTE 6: Tipos de Combustível**

**Objetivo:** Verificar diferenciação visual

**Passos:**
1. Adicione mais abastecimentos alternando entre Gasolina e Etanol
2. Observe a lista

**✅ Esperado:**
- Cada card mostra o tipo correto (Gasolina/Etanol)
- Tipo exibido em destaque com cor primária

---

### **TESTE 7: Observações**

**Objetivo:** Verificar campo opcional

**Passos:**
1. Adicione um abastecimento COM observações: "Abasteci no posto X"
2. Adicione outro abastecimento SEM observações

**✅ Esperado:**
- Card com observações mostra um divisor e a seção "Observações:"
- Card sem observações NÃO mostra essa seção

---

### **TESTE 8: Navegação**

**Objetivo:** Testar fluxo entre telas

**Passos:**
1. Na tela de histórico, toque (+)
2. Na tela de cadastro, toque "Cancelar"
3. Volta para histórico
4. Toque (+) novamente
5. Preencha e salve
6. Volta para histórico automaticamente

**✅ Esperado:** Navegação fluida, sem travamentos

---

### **TESTE 9: Persistência de Dados**

**Objetivo:** Verificar salvamento no banco

**Passos:**
1. Adicione 3-4 abastecimentos
2. **Feche o app completamente** (force stop)
3. Abra o app novamente

**✅ Esperado:**
- Todos os dados continuam lá
- Cálculos de consumo preservados
- Estatísticas corretas

---

### **TESTE 10: Rolagem da Lista**

**Objetivo:** Testar performance com múltiplos itens

**Passos:**
1. Adicione 10+ abastecimentos
2. Role a lista para cima e para baixo

**✅ Esperado:**
- Rolagem suave
- Cards renderizados corretamente
- Sem lag ou travamentos

---

## 📊 Casos de Teste com Dados Específicos

### Cenário Realista:

| Ordem | Data | KM | Litros | Preço/L | Tipo | Consumo Esperado |
|-------|------|-----|--------|---------|------|------------------|
| 1º | Hoje | 50.000 | 40 | 5.50 | Gasolina | - (primeiro) |
| 2º | Hoje +3h | 50.500 | 35 | 5.60 | Gasolina | 12,50 km/l |
| 3º | Hoje +1 dia | 51.000 | 38 | 5.55 | Etanol | 14,29 km/l |
| 4º | Hoje +2 dias | 51.450 | 30 | 4.20 | Etanol | 11,84 km/l |
| 5º | Hoje +3 dias | 51.900 | 32 | 5.65 | Gasolina | 15,00 km/l |

**Consumo médio esperado:** 13,41 km/l

---

## 🐛 Bugs Conhecidos a Verificar

- [ ] App crasha ao adicionar abastecimento?
- [ ] Cálculo de consumo está correto?
- [ ] Média de consumo está correta?
- [ ] Validações funcionam?
- [ ] Dados persistem após fechar app?
- [ ] Interface responsiva?
- [ ] Botão (+) sempre visível?

---

## ✅ Checklist Final

- [ ] Tela vazia funciona
- [ ] Adicionar primeiro abastecimento funciona
- [ ] Cálculo de consumo está correto
- [ ] Média de consumo está correta
- [ ] Validações funcionam
- [ ] Navegação funciona
- [ ] Persistência funciona
- [ ] Tipos de combustível diferenciados
- [ ] Observações aparecem corretamente
- [ ] Interface bonita e clara
- [ ] Performance boa com múltiplos itens

---

## 🎯 Como Reportar Problemas

Se encontrar algum problema, anote:
1. **O que você fez** (passos)
2. **O que esperava** acontecer
3. **O que realmente** aconteceu
4. **Screenshot** se possível

---

**Boa sorte nos testes! 🚗⛽📊**
