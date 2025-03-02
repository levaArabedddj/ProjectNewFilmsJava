package com.example.Enum;

public enum AdminPermission {
    FULL_ACCESS,        // Полный доступ (может делать все)
    PARTIAL_ACCESS,     // Частичный доступ (например, только к работе над фильмами и помощью для режиссера)
    INFORMATION_ACCESS  // Только просмотр информации, без изменений
}
