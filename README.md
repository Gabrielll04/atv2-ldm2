# Estocadao API

API REST simples em Kotlin Multiplatform com Ktor para controlar produtos e estoque usando Supabase/PostgreSQL.

## Como rodar

1. Abra este projeto no Android Studio.
2. Crie as tabelas no Supabase copiando o conteudo de `schema.sql` no SQL Editor.
3. Configure as variaveis de ambiente:

```bash
export SUPABASE_URL="https://seu-projeto.supabase.co"
export SUPABASE_KEY="sua-chave-do-supabase"
```

4. Rode pelo terminal:

```bash
./gradlew :server:run
```

No Android Studio, tambem da para rodar pela janela do Gradle em `server > Tasks > application > run`.

## Endpoints

Produtos:

```text
GET    /products
GET    /products/{id}
POST   /products
PUT    /products/{id}
DELETE /products/{id}
```

Estoque:

```text
GET    /stock
GET    /stock/{id}
POST   /stock
PUT    /stock/{id}
DELETE /stock/{id}
GET    /stock/summary
```

## Exemplos de JSON

Criar produto:

```json
{
  "name": "Caneta Azul",
  "description": "Caneta esferografica azul",
  "sku": "CAN-AZUL-001",
  "category": "Papelaria"
}
```

Criar estoque:

```json
{
  "product_id": "uuid-do-produto",
  "quantity": 50,
  "unit_price": 2.5,
  "location": "Prateleira A"
}
```

Resumo:

```json
[
  {
    "product_id": "uuid-do-produto",
    "product_name": "Caneta Azul",
    "total_quantity": 50
  }
]
```

## Observacoes

O `stock_summary` fica no banco e usa `GROUP BY + SUM`, como pedido na atividade. A FK de `stock_items.product_id` foi criada com `ON DELETE CASCADE`, entao ao apagar um produto os itens de estoque ligados a ele tambem sao apagados.
