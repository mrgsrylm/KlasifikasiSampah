class DatetimeConverters {
  static final DatetimeConverters constants = ImageConverters._();
  factory DatetimeConverters() => constants;
  ImageConverters._();
  String convertToAsiaJakarta(int unixTimestampMillis) {

    DateTime dateTimeMillis = DateTime.fromMillisecondsSinceEpoch(unixTimestampMillis);    
    String result = DateFormat.yMd().add_Hms().format(dateTimeMillis.toLocal());

    return result;
  }
}